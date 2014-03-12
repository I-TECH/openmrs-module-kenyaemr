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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Privilege;
import org.openmrs.Provider;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.PasswordException;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.metadata.SecurityMetadata;
import org.openmrs.module.kenyaemr.validator.EmailAddressValidator;
import org.openmrs.module.kenyaemr.validator.TelephoneNumberValidator;
import org.openmrs.module.kenyaemr.wrapper.PersonWrapper;
import org.openmrs.module.kenyaui.annotation.AppAction;
import org.openmrs.module.kenyaui.form.AbstractWebForm;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.validator.ValidateUtil;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * Create new account fragment controller
 */
public class NewAccountFragmentController {
	
	public void controller(@FragmentParam(value = "person", required = false) Person person,
						   FragmentModel model) {

		// Roles which can't be assigned directly to new users
		List<String> disallowedRoles = Arrays.asList(
				"Anonymous",
				"Authenticated",
				SecurityMetadata._Role.API_PRIVILEGES,
				SecurityMetadata._Role.API_PRIVILEGES_VIEW_AND_EDIT,
				SecurityMetadata._Role.SYSTEM_DEVELOPER
		);

		model.addAttribute("disallowedRoles", disallowedRoles);
		model.addAttribute("command", newCreateAccountForm(person));
	}

	/**
	 * Handles form submission
	 */
	@AppAction(EmrConstants.APP_ADMIN)
	public SimpleObject submit(@MethodParam("newCreateAccountForm") @BindParams CreateAccountForm form,
							   UiUtils ui,
							   HttpSession session,
							   @SpringBean KenyaUiUtils kenyaUi) {

		ui.validate(form, form, null);

		try {
			// This method will be typically called by a IT admin account that doesn't have all privileges. However, the
			// OpenMRS security model requires that you have a privilege to be able to grant it to others.
			for (Privilege priv : Context.getUserService().getAllPrivileges()) {
				Context.addProxyPrivilege(priv.getPrivilege());
			}

			Person person = form.save();

			kenyaUi.notifySuccess(session, "Account created");

			return SimpleObject.create("personId", person.getId());
		}
		finally {
			// Ensure that all proxy privileges are removed
			for (Privilege priv : Context.getUserService().getAllPrivileges()) {
				Context.removeProxyPrivilege(priv.getPrivilege());
			}
		}
	}
	
	public CreateAccountForm newCreateAccountForm(@RequestParam(value = "personId", required = false) Person person) {
		if (person != null) {
			return new CreateAccountForm(person);
		}
		else {
			return new CreateAccountForm();
		}
	}
	
	public class CreateAccountForm extends AbstractWebForm {

		private Person original;
		
		private PersonName personName;
		
		private String gender;

		private String telephoneContact;

		private String emailAddress;
		
		private String username;
		
		private String password;
		
		private String confirmPassword;
		
		private Set<Role> roles;
		
		private String providerIdentifier;
		
		public CreateAccountForm() {
			personName = new PersonName();
		}

		public CreateAccountForm(Person original) {
			this.original = original;

			this.personName = original.getPersonName();
			this.gender = original.getGender();

			PersonWrapper wrapper = new PersonWrapper(original);
			this.telephoneContact = wrapper.getTelephoneContact();
			this.emailAddress = wrapper.getEmailAddress();
		}
		
		/**
		 * @see org.openmrs.module.kenyaui.form.AbstractWebForm#validate(Object, org.springframework.validation.Errors)
		 */
		@Override
		public void validate(Object target, Errors errors) {
			// annoyingly, PersonNameValidator.validate doesn't behave right, so I can't do: validateField(errors, "personName");
			require(errors, "personName.givenName");
			require(errors, "personName.familyName");
			require(errors, "gender");
			require(errors, "telephoneContact");

			if (StringUtils.isNotBlank(telephoneContact)) {
				validateField(errors, "telephoneContact", new TelephoneNumberValidator());
			}
			if (StringUtils.isNotBlank(emailAddress)) {
				validateField(errors, "emailAddress", new EmailAddressValidator());
			}
			
			boolean hasUser = false;
			if (StringUtils.isNotEmpty(username)) {
				hasUser = true;
				if (Context.getUserService().getUserByUsername(username) != null) {
					errors.rejectValue("username", "kenyaemr.error.username.taken");
				}
				
				if (StringUtils.isEmpty(password)) {
					require(errors, "password");
				}
				else {
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

		/**
		 * @see org.openmrs.module.kenyaui.form.AbstractWebForm#save()
		 */
		@Override
		public Person save() {
			// Hopefully we caught any errors in the above validation, because any errors here will have ugly error messages
			Person person = createPerson();
			User user = getUser(person);
			Provider provider = getProvider(person);

			ValidateUtil.validate(person);

			if (user != null) {
				ValidateUtil.validate(user);
			}

			if (provider != null) {
				ValidateUtil.validate(provider);
			}

			Context.getPersonService().savePerson(person);

			if (user != null) {
				Context.getUserService().saveUser(user, getPassword());
			}
			if (provider != null) {
				Context.getProviderService().saveProvider(provider);
			}

			return person;
		}

		/**
		 * Creates a new person to be saved
		 * @return the person
		 */
		public Person createPerson() {
			Person ret = original != null ? original : new Person();
			ret.addName(personName);
			ret.setGender(gender);

			PersonWrapper wrapper = new PersonWrapper(ret);
			wrapper.setTelephoneContact(telephoneContact);
			wrapper.setEmailAddress(emailAddress);

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

		public Person getOriginal() {
			return original;
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
		 * @return the telephone
		 */
		public String getTelephoneContact() {
			return telephoneContact;
		}

		/**
		 * @param telephoneContact the telephone
		 */
		public void setTelephoneContact(String telephoneContact) {
			this.telephoneContact = telephoneContact;
		}

		public String getEmailAddress() {
			return emailAddress;
		}

		public void setEmailAddress(String emailAddress) {
			this.emailAddress = emailAddress;
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
