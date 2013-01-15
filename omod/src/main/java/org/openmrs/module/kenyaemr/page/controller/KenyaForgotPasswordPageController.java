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

import com.mysql.jdbc.StringUtils;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.AppUiUtil;
import org.openmrs.module.kenyaemr.KenyaEmrConstants;
import org.openmrs.module.kenyaemr.KenyaEmrWebConstants;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.session.Session;
import org.openmrs.util.PrivilegeConstants;
import org.openmrs.web.WebConstants;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Forgot password page controller
 */
public class KenyaForgotPasswordPageController {

	/**
	 * The mapping from user's IP address to the number of attempts at logging in from that IP
	 */
	private Map<String, Integer> loginAttemptsByIP = new HashMap<String, Integer>();

	/**
	 * The mapping from user's IP address to the time that they were locked out
	 */
	private Map<String, Date> lockoutDateByIP = new HashMap<String, Date>();

	public String controller(PageModel model,
						   Session session,
						   @RequestParam(value = "uname", required = false) String username,
						   @RequestParam(value = "secretAnswer", required = false) String secretAnswer,
						   HttpServletRequest request) {

		AppUiUtil.endCurrentApp(session);

		model.addAttribute("username", username);
		model.addAttribute("secretQuestion", null);

		if (!StringUtils.isNullOrEmpty(username)) {
			return handleSubmission(model, username, secretAnswer, request);
		}

		return null;
	}

	private String handleSubmission(PageModel model, String username, String secretAnswer, HttpServletRequest request) {

		HttpSession httpSession = request.getSession();

		// User already has a reset password... go change it...
		if (httpSession.getAttribute(KenyaEmrWebConstants.SESSION_ATTR_RESET_PASSWORD) != null) {
			return "redirect:/" + KenyaEmrConstants.MODULE_ID + "/profile.page";
		}

		String ipAddress = request.getLocalAddr();
		Integer forgotPasswordAttempts = loginAttemptsByIP.get(ipAddress);
		if (forgotPasswordAttempts == null)
			forgotPasswordAttempts = 1;

		boolean lockedOut = false;

		if (forgotPasswordAttempts > KenyaEmrWebConstants.MAX_ALLOWED_LOGIN_ATTEMPTS) {
			lockedOut = true;

			Date lockedOutTime = lockoutDateByIP.get(ipAddress);
			if (lockedOutTime != null && System.currentTimeMillis() - lockedOutTime.getTime() > KenyaEmrWebConstants.FAILED_LOGIN_LOCKOUT_TIME) {
				lockedOut = false;
				forgotPasswordAttempts = 0;
				lockoutDateByIP.put(ipAddress, null);
			} else {
				// they haven't been locked out before, or they're trying again
				// within the time limit.  Set the locked-out date to right now
				lockoutDateByIP.put(ipAddress, new Date());
			}

		}

		if (lockedOut) {
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "auth.forgotPassword.tooManyAttempts");
		} else {
			// if the previous logic didn't determine that the user should be locked out,
			// then continue with the check

			forgotPasswordAttempts++;

			if (StringUtils.isNullOrEmpty(secretAnswer)) {
				// if they are seeing this page for the first time

				User user = null;

				try {
					Context.addProxyPrivilege(PrivilegeConstants.VIEW_USERS);

					// Only search if they actually put in a username
					if (!StringUtils.isNullOrEmpty(username)) {
						user = Context.getUserService().getUserByUsername(username);
					}
				}
				finally {
					Context.removeProxyPrivilege(PrivilegeConstants.VIEW_USERS);
				}

				if (user == null || StringUtils.isNullOrEmpty(user.getSecretQuestion())) {
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "auth.question.empty");
				} else {
					model.addAttribute("secretQuestion", user.getSecretQuestion());

					// reset the forgotPasswordAttempts because they have a right user.
					// they will now have 5 more chances to get the question right
					forgotPasswordAttempts = 0;
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
				if (user == null || StringUtils.isNullOrEmpty(user.getSecretQuestion())) {
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "auth.question.empty");
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

					httpSession.setAttribute(KenyaEmrWebConstants.SESSION_ATTR_RESET_PASSWORD, randomPassword);
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "auth.password.reset");
					Context.authenticate(username, randomPassword);
					httpSession.setAttribute("loginAttempts", 0);
					return "redirect:/" + KenyaEmrConstants.MODULE_ID + "/profile.page";
				} else {
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "auth.answer.invalid");
					model.addAttribute("secretQuestion", user.getSecretQuestion());
				}
			}
		}

		loginAttemptsByIP.put(ipAddress, forgotPasswordAttempts);
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