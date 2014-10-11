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
package org.openmrs.module.kenyaemr.fragment.controller;

import org.openmrs.User;
import org.openmrs.api.PasswordException;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.kenyaui.form.ValidatingCommandObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * My profile page controller
 */
public class ProfileLoginDetailsFragmentController {

	public void controller(@FragmentParam(value = "tempPassword", required = false) String tempPassword,
						   FragmentModel model) {

		model.addAttribute("user", Context.getAuthenticatedUser());

		model.addAttribute("changePasswordForm", newChangePasswordForm(tempPassword));
		model.addAttribute("changeSecretQuestionForm", newChangeSecretQuestionForm());
	}

	/**
	 * Changes the current user's password
	 * @param form the edit form
	 * @param ui the ui utils
	 * @return the result
	 */
	public Object changePassword(@MethodParam("newChangePasswordForm") @BindParams("changePasswordForm") ChangePasswordForm form, UiUtils ui) {
		ui.validate(form, form, "changePasswordForm");

		try {
			Context.getUserService().changePassword(form.getOldPassword(), form.getNewPassword());
			Context.refreshAuthenticatedUser();
		}
		catch (DAOException ex) {
		   	return new FailureResult("Old password is incorrect");
		}

		return new SuccessResult("Updated password");
	}

	/**
	 * Changes the current user's secret question and answer
	 * @param form the edit form
	 * @param ui the ui utils
	 * @return the result
	 */
	public Object changeSecretQuestion(@MethodParam("newChangeSecretQuestionForm") @BindParams("changeSecretQuestionForm") ChangeSecretQuestionForm form, UiUtils ui) {
		ui.validate(form, form, "changeSecretQuestionForm");

		try {
			Context.getUserService().changeQuestionAnswer(form.getCurrentPassword(), form.getSecretQuestion(), form.getNewSecretAnswer());
			Context.refreshAuthenticatedUser();
		}
		catch (DAOException ex) {
			return new FailureResult("Current password is incorrect");
		}

		return new SuccessResult("Updated secret question");
	}

	/**
	 * Creates an instance of the change password form
	 * @return the form
	 */
	public ChangePasswordForm newChangePasswordForm(String tempPassword) {
		return new ChangePasswordForm(Context.getAuthenticatedUser(), tempPassword);
	}

	/**
	 * Creates an instance of the change secret question and answer form
	 * @return the form
	 */
	public ChangeSecretQuestionForm newChangeSecretQuestionForm() {
		return new ChangeSecretQuestionForm(Context.getAuthenticatedUser());
	}

	/**
	 * Form for changing current user's password
	 */
	public class ChangePasswordForm extends ValidatingCommandObject {

		private User user;
		private String oldPassword;
		private String newPassword;
		private String confirmNewPassword;

		public ChangePasswordForm(User user, String currentPassword) {
			this.user = user;
			this.oldPassword = currentPassword;
		}

		@Override
		public void validate(Object target, Errors errors) {
			ChangePasswordForm form = (ChangePasswordForm) target;

			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "oldPassword", "kenyaemr.error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPassword", "kenyaemr.error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmNewPassword", "kenyaemr.error.required");


			if (!errors.hasErrors()) {
				if (form.getNewPassword().equals(form.getConfirmNewPassword())) {
					try {
						OpenmrsUtil.validatePassword(user.getUsername(), form.getNewPassword(), user.getSystemId());
					}
					catch (PasswordException e) {
						errors.rejectValue("newPassword", e.getMessage());
					}
				} else {
					errors.rejectValue("confirmNewPassword", "kenyaemr.error.confirmPassword.match");
				}
			}
		}

		public String getOldPassword() {
			return oldPassword;
		}

		public void setOldPassword(String oldPassword) {
			this.oldPassword = oldPassword;
		}

		public String getNewPassword() {
			return newPassword;
		}

		public void setNewPassword(String newPassword) {
			this.newPassword = newPassword;
		}

		public String getConfirmNewPassword() {
			return confirmNewPassword;
		}

		public void setConfirmNewPassword(String confirmNewPassword) {
			this.confirmNewPassword = confirmNewPassword;
		}
	}

	/**
	 * Command object for editing user logins
	 */
	public class ChangeSecretQuestionForm extends ValidatingCommandObject {

		private User user;

		private String currentPassword;

		private String secretQuestion;

		private String newSecretAnswer;

		public ChangeSecretQuestionForm(User user) {
			this.user = user;
			this.secretQuestion = user.getSecretQuestion();
		}

		@Override
		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentPassword", "kenyaemr.error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secretQuestion", "kenyaemr.error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newSecretAnswer", "kenyaemr.error.required");
		}

		public String getCurrentPassword() {
			return currentPassword;
		}

		public void setCurrentPassword(String currentPassword) {
			this.currentPassword = currentPassword;
		}

		public String getSecretQuestion() {
			return secretQuestion;
		}

		public void setSecretQuestion(String secretQuestion) {
			this.secretQuestion = secretQuestion;
		}

		public String getNewSecretAnswer() {
			return newSecretAnswer;
		}

		public void setNewSecretAnswer(String newSecretAnswer) {
			this.newSecretAnswer = newSecretAnswer;
		}
	}
}