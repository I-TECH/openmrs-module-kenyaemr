/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.patient;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaui.form.ValidatingCommandObject;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller for editing patient relationships in the registration app
 */
public class EditCaseManagerFragmentController {

	public void controller(@FragmentParam(value = "relationship", required = false) Relationship relationship,
						   @FragmentParam("returnUrl") String returnUrl,
						   PageModel model) {

		Patient patient = (Patient) model.getAttribute(EmrWebConstants.MODEL_ATTR_CURRENT_PATIENT);

		model.addAttribute("command", new EditRelationshipForm(relationship, patient));
		model.addAttribute("typeOptions", getTypeOptions());
		model.addAttribute("returnUrl", returnUrl);
		//create list of providers
		List<Provider> providers = Context.getProviderService().getAllProviders();
		model.addAttribute("providersList", providers);
	}

	public Object saveRelationship(@MethodParam("getEditRelationshipForm") @BindParams EditRelationshipForm form,
								   UiUtils ui) {
		ui.validate(form, form, null);
		form.save();

		return new SuccessResult("Saved relationship");
	}

	public EditRelationshipForm getEditRelationshipForm(
			@RequestParam(value = "existingId", required = false) Relationship existing,
			@RequestParam("patientId") Patient patient) {
		return new EditRelationshipForm(existing, patient);
	}

	public class EditRelationshipForm extends ValidatingCommandObject {

		private Relationship existing;

		private Patient patient;

		private String isToPatient;

		private Person person;

		private String providerId;

		private Provider provider;

		private Date startDate;

		private Date endDate;

		public EditRelationshipForm(Relationship existing, Patient patient) {
			this.existing = existing;
			this.patient = patient;

			if (existing != null) {
				this.person = existing.getPersonA().equals(patient) ? existing.getPersonB() : existing.getPersonA();
				this.startDate = existing.getStartDate();
				this.endDate = existing.getEndDate();

				RelationshipType type = existing.getRelationshipType();

				if (type.getaIsToB().equals(type.getbIsToA())) { // e.g. Sibling
					this.isToPatient = existing.getRelationshipType().getId() + "";
				}
				else {
					char personSide = existing.getPersonA().equals(patient) ? 'B' : 'A';
					this.isToPatient = existing.getRelationshipType().getId() + ":" + personSide;
				}
			}
		}

		/**
		 * @see ValidatingCommandObject#validate(Object, Errors)
		 */
		@Override
		public void validate(Object o, Errors errors) {
			require(errors, "providerId");
			require(errors, "isToPatient");

			Date today = new Date();
			if(startDate != null && startDate.after(today)) {
				errors.rejectValue("startDate", "Relationship start date can't be in the future");
			}
			if(startDate == null) {
				errors.rejectValue("startDate", "Relationship start date can't be empty");
			}
			if(endDate != null && startDate != null && endDate.before(startDate)) {
				errors.rejectValue("endDate", "Relationship end date can't be before start date");
			}
			if(endDate != null && endDate.after(today)) {
				errors.rejectValue("endDate", "Relationship end date can't be in the future");
			}
		}

		/**
		 * Saves the form
		 */
		public void save() {
			Relationship rel = (existing != null) ? existing : new Relationship();
			RelationshipType type;
			Person personA,personB;

		 // Doesn't matter who is A or B, since case manager is one way for patients
	    	// We do not assign relationships to providers
				type = Context.getPersonService().getRelationshipTypeByUuid(isToPatient);
				personA = patient;
				personB = Context.getProviderService().getProvider(Integer.valueOf(providerId)).getPerson();

			rel.setRelationshipType(type);
			rel.setPersonA(personA);
			rel.setPersonB(personB);
			rel.setStartDate(startDate);
			rel.setEndDate(endDate);

			Context.getPersonService().saveRelationship(rel);
		}

		public Relationship getExisting() {
			return existing;
		}

		public Patient getPatient() {
			return patient;
		}

		public Person getPerson() {
			return person;
		}

		public void setPerson(Person person) {
			this.person = person;
		}

		public Provider getProvider() {
			return provider;
		}

		public void setProvider(Provider provider) {
			this.provider = provider;
		}

		public String getProviderId() {
			return providerId;
		}

		public void setProviderId(String providerId) {
			this.providerId = providerId;
		}

		public String getIsToPatient() {
			return isToPatient;
		}

		public void setIsToPatient(String isToPatient) {
			this.isToPatient = isToPatient;
		}


		public Date getStartDate() {
			return startDate;
		}

		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		public Date getEndDate() {
			return endDate;
		}

		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}
	}

	/**
	 * Creates a list of encoded relationship type options
	 * @return the options as simple objects {id, label}
	 */
	protected List<SimpleObject> getTypeOptions() {
		List<SimpleObject> options = new ArrayList<SimpleObject>();
		PersonService service = Context.getPersonService();
		RelationshipType caseManagerRelType = service.getRelationshipTypeByUuid(CommonMetadata._RelationshipType.CASE_MANAGER);

		for (RelationshipType type : Context.getPersonService().getAllRelationshipTypes()) {
			if (type.equals(caseManagerRelType)) {
				if (type.getaIsToB().equals(type.getbIsToA())) {
					options.add(SimpleObject.create("value", type.getId() + "", "label", type.getaIsToB()));
				} else {
					options.add(SimpleObject.create("value", type.getId() + ":A", "label", type.getaIsToB()));
					options.add(SimpleObject.create("value", type.getId() + ":B", "label", type.getbIsToA()));
				}
			}
		}
		return options;
	}
}