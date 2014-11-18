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
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaui.annotation.AppAction;
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
 * Editable Provider details
 */
public class ProviderDetailsFragmentController {

	public void controller(@FragmentParam("person") Person person,
						   @FragmentParam(value = "provider", required = false) Provider provider,
						   FragmentModel model) {

		model.addAttribute("person", person);
		model.addAttribute("provider", provider);
		model.addAttribute("form", newEditProviderDetailsForm(provider));
	}

	@AppAction(EmrConstants.APP_ADMIN)
	public Object submit(@RequestParam("personId") Person person,
									  @MethodParam("newEditProviderDetailsForm") @BindParams("provider") EditProviderDetailsForm form,
									  UiUtils ui) {
		ui.validate(form, form, "provider");
		form.save(person);
		return new SuccessResult("Saved provider details");
	}

	public EditProviderDetailsForm newEditProviderDetailsForm(@RequestParam(required = false, value = "provider.providerId") Provider provider) {
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

		public void save(Person person) {
			Context.getProviderService().saveProvider(getProviderToSave(person));
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
}