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

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.PasswordException;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 */
public class AdminEditUserFragmentController {
	
	public void controller(PageModel sharedPageModel, FragmentModel model) {
		User user = (User) sharedPageModel.getAttribute("user");
		model.addAttribute("user", user);
		model.addAttribute("editLoginDetails", newEditLoginDetailsForm(user));
		model.addAttribute("editPersonDetails", newEditPersonDetailsForm(user.getPerson()));
	}
	
	public Object retireUser(@RequestParam("userId") User user) {
		Context.getUserService().retireUser(user, null);
		return new SuccessResult("Disabled: " + user.getUsername());
	}
	
	public Object unretireUser(@RequestParam("userId") User user) {
		Context.getUserService().unretireUser(user);
		return new SuccessResult("Enabled: " + user.getUsername());
	}
	
	public Object editUserRoles(@RequestParam("userId") User user,
	                            @RequestParam(required=false, value="roles") Set<Role> roles) {
		if (roles.size() == 0) {
			return new FailureResult("At least one role is required");
		}
		user.setRoles(roles);
		Context.getUserService().saveUser(user, null);
		return new SuccessResult("Saved roles");
	}
	
	public Object editLoginDetails(@MethodParam("newEditLoginDetailsForm") @BindParams("editLoginDetails") EditLoginDetailsForm form,
	                               UiUtils ui) {
		ui.validate(form, form, null);
		Context.getUserService().saveUser(form.getUserToSave(),
		    form.getPassword().equals(form.PLACEHOLDER) ? null : form.getPassword());
		return new SuccessResult("Saved login details");
	}
	
	public Object editPersonDetails(@MethodParam("newEditPersonDetailsForm") @BindParams("editPersonDetails") EditPersonDetailsForm form,
	                               UiUtils ui) {
		ui.validate(form, form, null);
		Context.getPersonService().savePerson(form.getPersonToSave());
		return new SuccessResult("Saved person details");
	}
	
	public EditLoginDetailsForm newEditLoginDetailsForm(@RequestParam("editLoginDetails.userId") User u) {
		return new EditLoginDetailsForm(u);
	}
	
    public EditPersonDetailsForm newEditPersonDetailsForm(@RequestParam("editPersonDetails.personId") Person person) {
	    return new EditPersonDetailsForm(person);
    }

	
	public class EditPersonDetailsForm implements Validator {
		
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
		 * @see org.springframework.validation.Validator#supports(java.lang.Class)
		 */
		@Override
		public boolean supports(Class<?> clazz) {
			return clazz.equals(getClass());
		}
		
		/**
		 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
		 */
		@Override
		public void validate(Object target, Errors errors) {
		    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "personName.givenName", "error.requiredField");
		    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "personName.familyName", "error.requiredField");
		    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gender", "error.requiredField");
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
	
	public class EditLoginDetailsForm implements Validator {
		
		public String PLACEHOLDER = "XXXXXXXXXXXXXXXX";
		
		private User original;
		
		private Integer userId;
		
		private String username;
		
		private String password;
		
		private String confirmPassword;
		
		public EditLoginDetailsForm(User u) {
			this.original = u;
			this.userId = u.getUserId();
			this.username = u.getUsername();
			this.password = PLACEHOLDER;
			this.confirmPassword = PLACEHOLDER;
		}
		
		/**
		 * Assumes that this has passed validation already
		 * 
		 * @return
		 */
		public User getUserToSave() {
			original.setUsername(username);
			return original;
		}
		
		/**
		 * @see org.springframework.validation.Validator#supports(java.lang.Class)
		 */
		@Override
		public boolean supports(Class<?> clazz) {
			return clazz.equals(getClass());
		}
		
		/**
		 * @see org.springframework.validation.Validator#validate(java.lang.Object,
		 *      org.springframework.validation.Errors)
		 */
		@Override
		public void validate(Object target, Errors errors) {
			EditLoginDetailsForm command = (EditLoginDetailsForm) target;
			
			if (StringUtils.isEmpty(command.getUsername())) {
				errors.rejectValue("username", "error.requiredField");
			} else {
				if (!original.getUsername().equals(command.getUsername())
				        && Context.getUserService().getUserByUsername(command.getUsername()) != null) {
					errors.rejectValue("username", "kenyaemr.error.username.taken");
				}
			}
			
			if (StringUtils.isEmpty(command.getPassword())) {
				errors.rejectValue("password", "error.requiredField");
			} else {
				if (!PLACEHOLDER.equals(command.getPassword()) || !PLACEHOLDER.equals(command.getConfirmPassword())) {
					try {
						OpenmrsUtil.validatePassword(command.getUsername(), password, null);
					}
					catch (PasswordException e) {
						errors.rejectValue("password", e.getMessage());
					}
					if (StringUtils.isEmpty(command.getConfirmPassword())) {
						errors.rejectValue("confirmPassword", "error.requiredField");
					} else if (!command.getPassword().equals(command.getConfirmPassword())) {
						errors.rejectValue("confirmPassword", "kenyaemr.error.confirmPassword.match");
					}
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
		
	}
	
}
