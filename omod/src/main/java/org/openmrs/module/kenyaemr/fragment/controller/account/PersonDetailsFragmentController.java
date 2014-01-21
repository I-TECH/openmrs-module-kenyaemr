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

import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.wrapper.PersonWrapper;
import org.openmrs.module.kenyaui.annotation.AppAction;
import org.openmrs.module.kenyaui.validator.ValidatingCommandObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Editable Person details
 */
public class PersonDetailsFragmentController {

	public void controller(@FragmentParam("person") Person person,
						   FragmentModel model) {

		model.put("person", person);
		model.put("wrapped", new PersonWrapper(person));

		model.addAttribute("form", newEditPersonDetailsForm(person));
	}

	@AppAction(EmrConstants.APP_ADMIN)
	public Object submit(@MethodParam("newEditPersonDetailsForm") @BindParams("person") EditPersonDetailsForm form,
									UiUtils ui) {
		ui.validate(form, form, "person");
		form.save();
		return new SuccessResult("Saved person details");
	}

	public EditPersonDetailsForm newEditPersonDetailsForm(@RequestParam("person.personId") Person person) {
		return new EditPersonDetailsForm(person);
	}

	public class EditPersonDetailsForm extends ValidatingCommandObject {

		private Person original;

		private String telephone;

		public EditPersonDetailsForm(Person person) {
			this.original = person;

			this.telephone = new PersonWrapper(person).getTelephoneContact();
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

		/**
		 * Saves the form
		 */
		public void save() {
			new PersonWrapper(original).setTelephoneContact(telephone);

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
		 * Gets the telephone number
		 * @return the number
		 */
		public String getTelephone() {
			return telephone;
		}

		/**
		 * Sets the telephone number
		 * @param telephone the number
		 */
		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}
	}
}