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
import org.openmrs.*;
import org.openmrs.api.PasswordException;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaui.validator.ValidatingCommandObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

/**
 *
 */
public class EditAccountFragmentController {
	
	public void controller(PageModel sharedPageModel, FragmentModel model) {
		Person person = (Person) sharedPageModel.getAttribute("person");
		User user = (User) sharedPageModel.getAttribute("user");
		Provider provider = (Provider) sharedPageModel.getAttribute("provider");
		
		model.addAttribute("person", person);
		model.addAttribute("user", user);
		model.addAttribute("provider", provider);
		
		model.addAttribute("editPersonDetails", newEditPersonDetailsForm(person));
		model.addAttribute("editLoginDetails", newEditLoginDetailsForm(user));
		model.addAttribute("editProviderDetails", newEditProviderDetailsForm(provider));
	}

	public Object retireUser(@RequestParam("userId") User user) {
		Context.getUserService().retireUser(user, null);
		return new SuccessResult("Disabled: " + user.getUsername());
	}

	public Object unretireUser(@RequestParam("userId") User user) {
		Context.getUserService().unretireUser(user);
		return new SuccessResult("Enabled: " + user.getUsername());
	}
	
	public Object editPersonDetails(@MethodParam("newEditPersonDetailsForm") @BindParams("editPersonDetails") EditPersonDetailsForm form,
	                                UiUtils ui) {
		ui.validate(form, form, "editPersonDetails");
		Context.getPersonService().savePerson(form.getPersonToSave());
		return new SuccessResult("Saved person details");
	}
	
	public Object editLoginDetails(@RequestParam("personId") Person person,
	                               @MethodParam("newEditLoginDetailsForm") @BindParams("editLoginDetails") EditLoginDetailsForm form,
	                               UiUtils ui) {
		ui.validate(form, form, "editLoginDetails");

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
	
	public Object editProviderDetails(@RequestParam("personId") Person person,
	                                  @MethodParam("newEditProviderDetailsForm") @BindParams("editProviderDetails") EditProviderDetailsForm form,
	                                  UiUtils ui) {
		ui.validate(form, form, "editProviderDetails");
		Context.getProviderService().saveProvider(form.getProviderToSave(person));
		return new SuccessResult("Saved provider details");
	}
	
	public EditProviderDetailsForm newEditProviderDetailsForm(@RequestParam(required = false, value = "editProviderDetails.providerId") Provider provider) {
		return new EditProviderDetailsForm(provider);
	}
	
	public class EditProviderDetailsForm extends ValidatingCommandObject {
		
		private Provider original;
		
		private String identifier;
		
		public EditProviderDetailsForm(Provider provider) {
			this.original = provider;
			if (provider != null) {
				this.identifier = provider.getIdentifier();
			}
		}
		
		public Provider getProviderToSave(Person person) {
			Provider ret;
			if (original != null) {
				ret = original;
			} else {
				ret = new Provider();
				ret.setPerson(person);
			}
			ret.setIdentifier(identifier);
			return ret;
		}
		
		/**
		 * @see org.springframework.validation.Validator#validate(java.lang.Object,
		 *      org.springframework.validation.Errors)
		 */
		@Override
		public void validate(Object target, Errors errors) {
			require(errors, "identifier");
			if (identifier != null) {
				Provider withId = Context.getProviderService().getProviderByIdentifier(identifier);
				if (withId != null && !withId.equals(original)) {
					errors.rejectValue("identifier", "kenyaemr.error.providerIdentifier.taken");
				}
			}
		}
		
		/**
		 * @return the identifier
		 */
		public String getIdentifier() {
			return identifier;
		}
		
		/**
		 * @param identifier the identifier to set
		 */
		public void setIdentifier(String identifier) {
			this.identifier = identifier;
		}
		
	}
	
	public EditPersonDetailsForm newEditPersonDetailsForm(@RequestParam("editPersonDetails.personId") Person person) {
		return new EditPersonDetailsForm(person);
	}
	
	public class EditPersonDetailsForm extends ValidatingCommandObject {
		
		private Person original;
		
		public EditPersonDetailsForm(Person person) {
			this.original = person;
		}
		
		/**
		 * Assumes already validated
		 * 
		 * @return
		 */
		public Person getPersonToSave() {
			return original;
		}
		
		/**
		 * @see org.springframework.validation.Validator#validate(java.lang.Object,
		 *      org.springframework.validation.Errors)
		 */
		@Override
		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "personName.givenName", "kenyaemr.error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "personName.familyName", "kenyaemr.error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gender", "kenyaemr.error.required");
		}
		
		public Integer getPersonId() {
			return original.getPersonId();
		}
		
		/**
		 * @return the personName
		 */
		public PersonName getPersonName() {
			return original.getPersonName();
		}
		
		/**
		 * @return the gender
		 */
		public String getGender() {
			return original.getGender();
		}
		
		/**
		 * @param gender the gender to set
		 */
		public void setGender(String gender) {
			original.setGender(gender);
		}
		
		/**
		 * @return the original
		 */
		public Person getOriginal() {
			return original;
		}
	}
	
	public EditLoginDetailsForm newEditLoginDetailsForm(@RequestParam(required = false, value = "editLoginDetails.userId") User u) {
		return new EditLoginDetailsForm(u);
	}

	/**
	 * Command object for editing user logins
	 */
	public class EditLoginDetailsForm extends ValidatingCommandObject {
		
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
		public EditLoginDetailsForm(User original) {
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
			EditLoginDetailsForm command = (EditLoginDetailsForm) target;
			
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