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

package org.openmrs.module.kenyaemr.page.controller;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.openmrs.ui.framework.Link;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for relationship edit page
 */
@SharedPage({EmrConstants.APP_REGISTRATION, EmrConstants.APP_INTAKE, EmrConstants.APP_CLINICIAN})
public class FamilyAndPartnerTestingPageController {

	public void controller(@RequestParam(value="patientId") Patient patient,
						   @RequestParam("returnUrl") String returnUrl,
						   @SpringBean KenyaUiUtils kenyaUi,
						   UiUtils ui,
						   PageRequest pageRequest,
						   PageModel model) {

		// Get all relationships as simple objects
        // patient id, name, sex, age, relation, test date, test result, enrolled, art number, initiated, status
        PatientService patientService = Context.getPatientService();
        EncounterService encounterService = Context.getEncounterService();

		List<SimpleObject> relationships = new ArrayList<SimpleObject>();
		for (Relationship relationship : Context.getPersonService().getRelationshipsByPerson(patient)) {
			Person person = null;
			String type = null;
			Integer age = null;
			Date testDate = null;
			String testResult = null;
			Date dateEnrolled = null;
			PatientIdentifier UPN = null;
			Boolean alive = null;

			if (patient.equals(relationship.getPersonA())) {
				person = relationship.getPersonB();
				type = relationship.getRelationshipType().getbIsToA();

			}
			else if (patient.equals(relationship.getPersonB())) {
				person = relationship.getPersonA();
				type = relationship.getRelationshipType().getaIsToB();
			}

			String genderCode = person.getGender().toLowerCase();
			String linkUrl, linkIcon;
			age = person.getAge();
			PatientIdentifierType pit = patientService.getPatientIdentifierTypeByUuid(Metadata.IdentifierType.UNIQUE_PATIENT_NUMBER);
			List<PatientIdentifier> identifierList = patientService.getPatientIdentifiers(null, Arrays.asList(pit), null, Arrays.asList(patientService.getPatient(person.getId())),null);
			UPN = identifierList.get(0);
			alive = person.isDead();


			if (person.isPatient()) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("patientId", person.getId());
				params.put("appId", "kenyaemr.medicalEncounter");
				params.put("returnUrl", returnUrl);
				linkUrl = ui.pageLink(pageRequest.getProviderName(), pageRequest.getPageName(), params);
				linkIcon = ui.resourceLink("kenyaui", "images/glyphs/patient_" + genderCode + ".png");
			}
			else {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("personId", person.getId());
                params.put("appId", "kenyaemr.medicalEncounter");
                params.put("returnUrl", returnUrl);
				linkUrl = ui.pageLink(EmrConstants.MODULE_ID, "admin/editAccount", params);
				linkIcon = ui.resourceLink("kenyaui", "images/glyphs/person_" + genderCode + ".png");
			}

			Link link = new Link(kenyaUi.formatPersonName(person), linkUrl, linkIcon);

			relationships.add(SimpleObject.create(
					"relationshipId", relationship.getId(),
					"type", type,
					"personLink", link,
                    "age" , age,
                    "status", alive? "Dead": "Alive",
                    "art_no", UPN != null? UPN.toString(): ""
			));
		}

		model.addAttribute("patient", patient);
		model.addAttribute("relationships", relationships);
		model.addAttribute("returnUrl", returnUrl);
	}
}