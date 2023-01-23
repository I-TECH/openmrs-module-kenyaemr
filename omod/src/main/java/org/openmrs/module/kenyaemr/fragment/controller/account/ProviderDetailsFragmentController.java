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
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.ProviderAttribute;
import org.openmrs.User;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Editable Provider details
 */
public class ProviderDetailsFragmentController {
		
	public void controller(@FragmentParam("person") Person person,
	        @FragmentParam(value = "provider", required = false) Provider provider, FragmentModel model) {
		Location primaryFacility = null;
		
		List<ProviderAttribute> attributes = new ArrayList<ProviderAttribute>(provider.getActiveAttributes());
		if (attributes.size() > 0) {
			for (ProviderAttribute attribute : attributes) {
				if (attribute.getAttributeType().getUuid().equals(CommonMetadata._ProviderAttributeType.PRIMARY_FACILITY)) {
					primaryFacility = (Location) attribute.getValue();
				}
			}
		}
		List<Location> facilities = Context.getLocationService().getAllLocations();
		
		model.addAttribute("person", person);
		model.addAttribute("provider", provider);
		model.addAttribute("primaryFacility", primaryFacility);
		model.addAttribute("form", newEditProviderDetailsForm(provider, person));
	}
	
	@AppAction(EmrConstants.APP_ADMIN)
	public Object submit(@RequestParam("personId") Person person,
	        @MethodParam("newEditProviderDetailsForm") @BindParams("provider") EditProviderDetailsForm form, UiUtils ui) {
		ui.validate(form, form, "provider");
		form.save(person);
		return new SuccessResult("Saved provider details");
	}
	
	public EditProviderDetailsForm newEditProviderDetailsForm(
	        @RequestParam(required = false, value = "provider.providerId") Provider provider, Person person) {
		return new EditProviderDetailsForm(provider, person);
	}
	
	public class EditProviderDetailsForm extends ValidatingCommandObject {
		
		private Provider original;
		
		private String identifier;
		
		private Person origPerson;
		
		private String providerFacility;
		
		public String getProviderFacility() {
			return providerFacility;
		}
		
		public void setProviderFacility(String providerFacility) {
			this.providerFacility = providerFacility;
		}
		
		public EditProviderDetailsForm(Provider provider, Person person) {
			this.original = provider;
			this.origPerson = person;
			if (provider != null) {
				this.identifier = provider.getIdentifier();
			} else {
				User thisUser = Context.getUserService().getUsersByPerson(person, false).get(0);
				this.identifier = thisUser.getSystemId();
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
			User thisUser = Context.getUserService().getUsersByPerson(person, false).get(0);
			String userSystemId = thisUser.getSystemId();
			ret.setIdentifier(userSystemId);
			
			ProviderAttribute attribute = getFacilityAttribute(providerFacility);
			if (attribute != null) {
				ret.setAttribute(attribute);
			}
			return ret;
		}
		
		ProviderAttribute getFacilityAttribute(String facility) {
			if (StringUtils.isEmpty(facility)) {
				return null;
			}
			ProviderAttribute primaryFacilityAttr = new ProviderAttribute();
			primaryFacilityAttr.setAttributeType(Context.getService(ProviderService.class)
			        .getProviderAttributeTypeByUuid(CommonMetadata._ProviderAttributeType.PRIMARY_FACILITY));
			primaryFacilityAttr.setValue(Context.getLocationService().getLocation(Integer.parseInt(facility)));
			return primaryFacilityAttr;
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
