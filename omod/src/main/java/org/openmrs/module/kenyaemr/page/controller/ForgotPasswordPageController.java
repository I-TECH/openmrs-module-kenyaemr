/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyaemr.page.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.module.kenyaemr.IPAccessSecurity;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.PublicPage;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Forgot password page controller
 */
@PublicPage
public class ForgotPasswordPageController {

	public String controller(PageModel model,
						   @RequestParam(value = "uname", required = false) String username,
						   @RequestParam(value = "secretAnswer", required = false) String secretAnswer,
						   @SpringBean KenyaUiUtils kenyaUi,
						   HttpServletRequest request) {

		model.addAttribute("username", username);
		model.addAttribute("secretQuestion", null);

		if (!StringUtils.isEmpty(username)) {
			return handleSubmission(model, username, secretAnswer, request, kenyaUi);
		}

		return null;
	}

	private String handleSubmission(PageModel model, String username, String secretAnswer, HttpServletRequest request, KenyaUiUtils kenyaUi) {

		HttpSession httpSession = request.getSession();

		// User already has a reset password... go change it...
		if (httpSession.getAttribute(EmrWebConstants.SESSION_ATTR_RESET_PASSWORD) != null) {
			return "redirect:/" + EmrConstants.MODULE_ID + "/profile.page";
		}

		String ipAddress = request.getRemoteAddr();

		if (IPAccessSecurity.isLockedOut(ipAddress)) {
			kenyaUi.notifyError(httpSession, "auth.forgotPassword.tooManyAttempts");
		} else {
			if (StringUtils.isEmpty(secretAnswer)) {
				// if they are seeing this page for the first time

				User user = null;

				try {
					Context.addProxyPrivilege(PrivilegeConstants.VIEW_USERS);

					// Only search if they actually put in a username
					if (!StringUtils.isEmpty(username)) {
						user = Context.getUserService().getUserByUsername(username);
					}
				}
				finally {
					Context.removeProxyPrivilege(PrivilegeConstants.VIEW_USERS);
				}

				if (user == null) {
					// Client might be trying to guess a username
					IPAccessSecurity.registerFailedAccess(ipAddress);
					kenyaUi.notifyError(httpSession, "auth.question.empty");
				} else if (StringUtils.isEmpty(user.getSecretQuestion())) {
					kenyaUi.notifyError(httpSession, "auth.question.empty");
				} else {
					model.addAttribute("secretQuestion", user.getSecretQuestion());
				}

			} else {
				// if they've filled in the username and entered their secret answer

				User user = null;

				try {
					Context.addProxyPrivilege(PrivilegeConstants.VIEW_USERS);
					user = Context.getUserService().getUserByUsername(username);
				}
				finally {
					Context.removeProxyPrivilege(PrivilegeConstants.VIEW_USERS);
				}

				// Check the secret question again in case the user got here "illegally"
				if (user == null || StringUtils.isEmpty(user.getSecretQuestion())) {
					kenyaUi.notifyError(httpSession, "auth.question.empty");
				} else if (user.getSecretQuestion() != null && Context.getUserService().isSecretAnswer(user, secretAnswer)) {

					String randomPassword = randomPassword();

					try {
						Context.addProxyPrivilege(PrivilegeConstants.EDIT_USER_PASSWORDS);
						Context.getUserService().changePassword(user, randomPassword);
						Context.refreshAuthenticatedUser();
					}
					finally {
						Context.removeProxyPrivilege(PrivilegeConstants.EDIT_USER_PASSWORDS);
					}

					IPAccessSecurity.registerSuccessfulAccess(ipAddress);
					httpSession.setAttribute(EmrWebConstants.SESSION_ATTR_RESET_PASSWORD, randomPassword);
					kenyaUi.notifySuccess(httpSession, "auth.password.reset");
					Context.authenticate(username, randomPassword);
					httpSession.setAttribute("loginAttempts", 0);
					return "redirect:/" + EmrConstants.MODULE_ID + "/profile.page";
				} else {
					kenyaUi.notifyError(httpSession, "auth.answer.invalid");
					model.addAttribute("secretQuestion", user.getSecretQuestion());
					IPAccessSecurity.registerFailedAccess(ipAddress);
				}
			}
		}

		return null;
	}

	/**
	 * Generates a random password
	 * @return the password
	 */
	private static String randomPassword() {
		StringBuilder randomPassword = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			randomPassword.append(String.valueOf((int)(Math.random() * (127 - 48) + 48)));
		}
		return randomPassword.toString();
	}
}