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

import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.PersonName;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.PasswordException;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 *
 */
public class AdminNewUserFragmentController {
	
	public void controller(FragmentModel model) {
		model.addAttribute("newUser", newUserCommandObject());
	}
	
	public SimpleObject createUser(@MethodParam("newUserCommandObject") @BindParams NewUserCommandObject command,
	                               UiUtils ui) {
		ui.validate(command, command, null);
		User user = Context.getUserService().saveUser(command.toUser(), command.getPassword());
		return SimpleObject.fromObject(user, ui, "userId", "username");
	}
	
	public NewUserCommandObject newUserCommandObject() {
		return new NewUserCommandObject();
	}
	
	public class NewUserCommandObject implements Validator {
		
		private PersonName personName;
		
		private String gender;
		
		private String username;
		
		private String password;
		
		private String confirmPassword;
		
		private List<Role> roles;
		
		public NewUserCommandObject() {
			personName = new PersonName();
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
			NewUserCommandObject command = (NewUserCommandObject) target;
			
		    ValidationUtils.rejectIfEmpty(errors, "personName.givenName", "error.requiredField");
		    ValidationUtils.rejectIfEmpty(errors, "personName.familyName", "error.requiredField");
		    ValidationUtils.rejectIfEmpty(errors, "gender", "error.requiredField");

		    if (StringUtils.isEmpty(command.getUsername())) {
		    	errors.rejectValue("username", "error.requiredField");
		    } else if (Context.getUserService().getUserByUsername(command.getUsername()) != null) {
		    	errors.rejectValue("username", "kenyaemr.error.username.taken");
		    }
		    
		    if (StringUtils.isEmpty(command.getPassword())) {
		    	errors.rejectValue("password", "error.requiredField");
		    } else {
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
		    
		    ValidationUtils.rejectIfEmpty(errors, "roles", "error.requiredField");
		}
		
		/**
		 * Assumes {@link #validate(Object, Errors)}) has been called first
		 * 
		 * @return
		 */
		public User toUser() {
			User ret = new User();
			ret.addName(getPersonName());
			ret.setUsername(getUsername());
			ret.getPerson().setGender(getGender());
			ret.setRoles(new HashSet<Role>(getRoles()));
			return ret;
		}
		
		/**
		 * @return the personName
		 */
		public PersonName getPersonName() {
			return personName;
		}
		
		/**
		 * @param personName the personName to set
		 */
		public void setPersonName(PersonName personName) {
			this.personName = personName;
		}
		
		/**
		 * @return the gender
		 */
		public String getGender() {
			return gender;
		}
		
		/**
		 * @param gender the gender to set
		 */
		public void setGender(String gender) {
			this.gender = gender;
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
		
		/**
		 * @return the roles
		 */
		public List<Role> getRoles() {
			return roles;
		}
		
		/**
		 * @param roles the roles to set
		 */
		public void setRoles(List<Role> roles) {
			this.roles = roles;
		}
		
	}
	
}
