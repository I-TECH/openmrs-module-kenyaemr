/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.account;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.validator.EmailAddressValidator;
import org.openmrs.module.kenyaemr.validator.TelephoneNumberValidator;
import org.openmrs.module.kenyaemr.wrapper.PersonWrapper;
import org.openmrs.module.kenyaui.form.ValidatingCommandObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Editable Person details
 */
public class PersonDetailsFragmentController {

	public void controller(@FragmentParam("person") Person person,
						   FragmentModel model) {

		model.addAttribute("person", person);
		model.addAttribute("form", newEditPersonDetailsForm(person));
	}

	/**
	 * Handles form submissions. Throws authentication exception if current app is not the admin app or user isn't the
	 * person being edited
	 */
	public Object submit(@MethodParam("newEditPersonDetailsForm") @BindParams("person") EditPersonDetailsForm form,
						 @RequestParam("person.personId") Person person,
						 UiUtils ui) {

		User currentUser = Context.getAuthenticatedUser();
		boolean isAdmin = currentUser.hasPrivilege("App: " + EmrConstants.APP_ADMIN);
		boolean isThisUser = currentUser.getPerson().equals(person);

		if (!(isAdmin || isThisUser)) {
			throw new APIAuthenticationException("Only admin app allows editing of another person's details");
		}

		ui.validate(form, form, "person");
		form.save();

		if (isThisUser) {
			Context.refreshAuthenticatedUser();
		}

		return new SuccessResult("Saved personal details");
	}

	public EditPersonDetailsForm newEditPersonDetailsForm(@RequestParam("person.personId") Person person) {
		return new EditPersonDetailsForm(person);
	}

	public class EditPersonDetailsForm extends ValidatingCommandObject {

		private Person original;

		private String telephoneContact;

		private String emailAddress;

		public EditPersonDetailsForm(Person person) {
			this.original = person;

			PersonWrapper wrapper = new PersonWrapper(person);

			this.telephoneContact = wrapper.getTelephoneContact();
			this.emailAddress = wrapper.getEmailAddress();
		}

		/**
		 * @see org.springframework.validation.Validator#validate(java.lang.Object,
		 *      org.springframework.validation.Errors)
		 */
		@Override
		public void validate(Object target, Errors errors) {
			require(errors, "personName.givenName");
			require(errors, "personName.familyName");
			require(errors, "gender");

			if (StringUtils.isNotBlank(telephoneContact)) {
				validateField(errors, "telephoneContact", new TelephoneNumberValidator());
			}

			if (StringUtils.isNotBlank(emailAddress)) {
				validateField(errors, "emailAddress", new EmailAddressValidator());
			}
		}

		/**
		 * Saves the form
		 */
		public void save() {
			PersonWrapper wrapper = new PersonWrapper(original);
			wrapper.setTelephoneContact(telephoneContact);
			wrapper.setEmailAddress(emailAddress);

			Context.getPersonService().savePerson(original);
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
		 * Gets the telephone contact
		 * @return the number
		 */
		public String getTelephoneContact() {
			return telephoneContact;
		}

		/**
		 * Sets the telephone contact
		 * @param telephoneContact the number
		 */
		public void setTelephoneContact(String telephoneContact) {
			this.telephoneContact = telephoneContact;
		}

		/**
		 * Gets the email address
		 * @return the address
		 */
		public String getEmailAddress() {
			return emailAddress;
		}

		/**
		 * Sets the email address
		 * @param emailAddress the address
		 */
		public void setEmailAddress(String emailAddress) {
			this.emailAddress = emailAddress;
		}
	}
}