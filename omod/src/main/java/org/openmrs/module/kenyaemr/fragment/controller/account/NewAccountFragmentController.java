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

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.PasswordException;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.ValidatingCommandObject;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.validator.ValidateUtil;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpSession;

/**
 * Create new account fragment controller
 */
public class NewAccountFragmentController {
	
	public void controller(FragmentModel model) {
		model.addAttribute("account", newAccountCommandObject());
	}
	
	public SimpleObject createAccount(@MethodParam("newAccountCommandObject") @BindParams NewAccountCommandObject command,
	                                  UiUtils ui, @SpringBean KenyaUiUtils kenyaUi, HttpSession session) {
		ui.validate(command, command, null);
		// hopefully we caught any errors in the above validation, because any errors here will have ugly error messages
		Person person = command.getPerson();
		User user = command.getUser(person);
		Provider provider = command.getProvider(person);
		
		ValidateUtil.validate(person);

		if (user != null) {
			ValidateUtil.validate(user);
		}

		if (provider != null) {
			ValidateUtil.validate(provider);
		}
		
		person = Context.getPersonService().savePerson(person);

		if (user != null) {
			user = Context.getUserService().saveUser(user, command.getPassword());
		}
		if (provider != null) {
			provider = Context.getProviderService().saveProvider(provider);
		}

		kenyaUi.notifySuccess(session, "Account created");
		
		return SimpleObject.create("personId", person.getPersonId());
	}
	
	public NewAccountCommandObject newAccountCommandObject() {
		return new NewAccountCommandObject();
	}
	
	public class NewAccountCommandObject extends ValidatingCommandObject {
		
		private PersonName personName;
		
		private String gender;
		
		private String username;
		
		private String password;
		
		private String confirmPassword;
		
		private Set<Role> roles;
		
		private String providerIdentifier;
		
		public NewAccountCommandObject() {
			personName = new PersonName();
		}
		
		/**
		 * @see org.springframework.validation.Validator#validate(java.lang.Object,
		 *      org.springframework.validation.Errors)
		 */
		@Override
		public void validate(Object target, Errors errors) {
			// annoyingly, PersonNameValidator.validate doesn't behave right, so I can't do: validateField(errors, "personName");
			require(errors, "personName.givenName");
			require(errors, "personName.familyName");
			require(errors, "gender");
			
			boolean hasUser = false;
			if (StringUtils.isNotEmpty(username)) {
				hasUser = true;
				if (Context.getUserService().getUserByUsername(username) != null) {
					errors.rejectValue("username", "kenyaemr.error.username.taken");
				}
				
				if (StringUtils.isEmpty(password)) {
					require(errors, "password");
				} else {
					try {
						OpenmrsUtil.validatePassword(username, password, null);
					}
					catch (PasswordException e) {
						errors.rejectValue("password", e.getMessage());
					}
				}
				
				if (StringUtils.isEmpty(confirmPassword)) {
					require(errors, "confirmPassword");
				} else if (!OpenmrsUtil.nullSafeEquals(password, confirmPassword)) {
					errors.rejectValue("confirmPassword", "kenyaemr.error.confirmPassword.match");
				}
				
				require(errors, "roles");
			}
			
			boolean hasProvider = false;
			if (StringUtils.isNotEmpty(providerIdentifier)) {
				hasProvider = true;
				Provider withId = Context.getProviderService().getProviderByIdentifier(providerIdentifier);
				if (withId != null) {
					errors.rejectValue("providerIdentifier", "kenyaemr.error.providerIdentifier.taken");
				}
			}
			
			if (!hasUser && !hasProvider) {
				errors.reject("Account must be a User, a Provider, or both");
			}
		}
		
		Person getPerson() {
			Person ret = new Person();
			ret.addName(personName);
			ret.setGender(gender);
			return ret;
		}
		
		User getUser(Person person) {
			if (StringUtils.isEmpty(username)) {
				return null;
			}
			User ret = new User();
			ret.setPerson(person);
			ret.setUsername(username);
			ret.setRoles(roles);
			return ret;
		}
		
		Provider getProvider(Person person) {
			if (StringUtils.isEmpty(providerIdentifier)) {
				return null;
			}
			Provider ret = new Provider();
			ret.setPerson(person);
			ret.setIdentifier(providerIdentifier);
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
		public Set<Role> getRoles() {
			return roles;
		}
		
		/**
		 * @param roles the roles to set
		 */
		public void setRoles(Set<Role> roles) {
			this.roles = roles;
		}
		
		/**
		 * @return the providerIdentifier
		 */
		public String getProviderIdentifier() {
			return providerIdentifier;
		}
		
		/**
		 * @param providerIdentifier the providerIdentifier to set
		 */
		public void setProviderIdentifier(String providerIdentifier) {
			this.providerIdentifier = providerIdentifier;
		}
		
	}
}
