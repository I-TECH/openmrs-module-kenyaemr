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

package org.openmrs.module.kenyaemr.fragment.controller.account;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.PasswordException;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.metadata.SecurityMetadata;
import org.openmrs.module.kenyaui.annotation.AppAction;
import org.openmrs.module.kenyaui.form.ValidatingCommandObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Editable User details
 */
public class UserDetailsFragmentController {

	public void controller(@FragmentParam("person") Person person,
						   @FragmentParam(value = "user", required = false) User user,
						   FragmentModel model) {

		// Roles which can't be assigned directly to users
		List<String> disallowedRoles = new ArrayList<String>(Arrays.asList(
				"Anonymous",
				"Authenticated",
				SecurityMetadata._Role.API_PRIVILEGES,
				SecurityMetadata._Role.API_PRIVILEGES_VIEW_AND_EDIT,
				"Provider"
		));

		// If user is not the admin account, don't let them become a super-user
		if (user == null || !"admin".equals(user.getSystemId())) {
			disallowedRoles.add(SecurityMetadata._Role.SYSTEM_DEVELOPER);
		}

		model.addAttribute("person", person);
		model.addAttribute("user", user);
		model.addAttribute("disallowedRoles", disallowedRoles);
		model.addAttribute("form", newEditUserDetailsForm(user));
	}

	@AppAction(EmrConstants.APP_ADMIN)
	public Object submit(@RequestParam("personId") Person person,
						 @MethodParam("newEditUserDetailsForm") @BindParams("user") EditUserDetailsForm form,
						 UiUtils ui) {

		ui.validate(form, form, "user");

		User user = form.getUserToSave(person);
		String newPassword = form.getPassword().equals(form.PLACEHOLDER) ? null : form.getPassword();

		if (user.getUserId() == null) {
			// New users can be saved with a password
			Context.getUserService().saveUser(user, newPassword);
		}
		else {
			Context.getUserService().saveUser(user, null);

			// To save a password for an original user, have to call changePassword
			if (newPassword != null) {
				Context.getUserService().changePassword(user, newPassword);
			}
		}

		// Update secret answer if specified
		if (!form.PLACEHOLDER.equals(form.getSecretAnswer())) {
			Context.getUserService().changeQuestionAnswer(user, form.getSecretQuestion(), form.getSecretAnswer());
		}

		return new SuccessResult("Saved login details");
	}

	public EditUserDetailsForm newEditUserDetailsForm(@RequestParam(required = false, value = "user.userId") User u) {
		return new EditUserDetailsForm(u);
	}

	/**
	 * Command object for editing user logins
	 */
	public class EditUserDetailsForm extends ValidatingCommandObject {

		public String PLACEHOLDER = "XXXXXXXXXXXXXXXX";

		private User original;

		private Integer userId;

		private String username;

		private String password;

		private String confirmPassword;

		private String secretQuestion;

		private String secretAnswer;

		private Set<Role> roles;

		/**
		 * Create new command object
		 * @param original the original user (may be null)
		 */
		public EditUserDetailsForm(User original) {
			this.original = original;
			if (original != null) {
				this.userId = original.getUserId();
				this.username = original.getUsername();
				this.password = PLACEHOLDER;
				this.confirmPassword = PLACEHOLDER;
				this.secretQuestion = original.getSecretQuestion();
				this.secretAnswer = PLACEHOLDER;
				this.roles = original.getRoles();
			}
		}

		/**
		 * Assumes that this has passed validation already
		 *
		 * @return
		 */
		public User getUserToSave(Person person) {
			User ret = original != null ? original : new User(person);
			ret.setUsername(username);
			ret.setRoles(roles);
			return ret;
		}

		/**
		 * @see org.springframework.validation.Validator#validate(java.lang.Object,
		 *      org.springframework.validation.Errors)
		 */
		@Override
		public void validate(Object target, Errors errors) {
			EditUserDetailsForm command = (EditUserDetailsForm) target;

			if (StringUtils.isEmpty(command.getUsername())) {
				errors.rejectValue("username", "kenyaemr.error.required");
			} else {
				if ((original == null || !command.getUsername().equals(original.getUsername()))
						&& Context.getUserService().getUserByUsername(command.getUsername()) != null) {
					errors.rejectValue("username", "kenyaemr.error.username.taken");
				}
			}

			if (StringUtils.isEmpty(command.getPassword())) {
				errors.rejectValue("password", "kenyaemr.error.required");
			} else {
				if (!PLACEHOLDER.equals(command.getPassword()) || !PLACEHOLDER.equals(command.getConfirmPassword())) {
					try {
						OpenmrsUtil.validatePassword(command.getUsername(), password, null);
					}
					catch (PasswordException e) {
						errors.rejectValue("password", e.getMessage());
					}
					if (StringUtils.isEmpty(command.getConfirmPassword())) {
						errors.rejectValue("confirmPassword", "kenyaemr.error.required");
					} else if (!command.getPassword().equals(command.getConfirmPassword())) {
						errors.rejectValue("confirmPassword", "kenyaemr.error.confirmPassword.match");
					}
				}
			}

			// Check if user changed secret question but not the answer as well - not allowed
			if (original != null
					&& !(command.getSecretQuestion().equals("") && original.getSecretQuestion() == null)
					&& !command.getSecretQuestion().equals(original.getSecretQuestion())
					&& PLACEHOLDER.equals(command.getSecretAnswer())) {
				errors.rejectValue("secretAnswer", "kenyaemr.error.secretAnswerNotChangedWithQuestion");
			}

			require(errors, "roles");

			if (original != null && "admin".equals(original.getSystemId())) {
				boolean hasSysDevRole = false;
				for (Role role : roles) {
					if (role.getRole().equals(SecurityMetadata._Role.SYSTEM_DEVELOPER)) {
						hasSysDevRole = true;
						break;
					}
				}

				if (!hasSysDevRole) {
					errors.rejectValue("roles", "Admin account must have " + SecurityMetadata._Role.SYSTEM_DEVELOPER + " role");
				}
			}
		}

		/**
		 * @return the userId
		 */
		public Integer getUserId() {
			return userId;
		}

		/**
		 * @param userId the userId to set
		 */
		public void setUserId(Integer userId) {
			this.userId = userId;
		}

		/**
		 * @return the username
		 */
		public String getUsername() {
			return username;
		}

		/**
		 * @param username the username to set
		 */
		public void setUsername(String username) {
			this.username = username;
		}

		/**
		 * @return the password
		 */
		public String getPassword() {
			return password;
		}

		/**
		 * @param password the password to set
		 */
		public void setPassword(String password) {
			this.password = password;
		}

		/**
		 * @return the confirmPassword
		 */
		public String getConfirmPassword() {
			return confirmPassword;
		}

		/**
		 * @param confirmPassword the confirmPassword to set
		 */
		public void setConfirmPassword(String confirmPassword) {
			this.confirmPassword = confirmPassword;
		}

		public String getSecretQuestion() {
			return secretQuestion;
		}

		public void setSecretQuestion(String secretQuestion) {
			this.secretQuestion = secretQuestion;
		}

		public String getSecretAnswer() {
			return secretAnswer;
		}

		public void setSecretAnswer(String secretAnswer) {
			this.secretAnswer = secretAnswer;
		}

		/**
		 * @return the roles
		 */
		public Set<Role> getRoles() {
			return roles;
		}

		/**
		 * @param roles the roles to set
		 */
		public void setRoles(Set<Role> roles) {
			this.roles = roles;
		}

	}
}