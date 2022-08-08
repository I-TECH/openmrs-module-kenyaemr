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
import org.openmrs.Relationship;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.ui.framework.Link;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for patient relationships panel
 */
public class PatientRelationshipsFragmentController {

	public void controller(@FragmentParam(value="patient") Patient patient,
						   @SpringBean KenyaUiUtils kenyaUi,
						   PageRequest pageRequest,
						   UiUtils ui,
						   FragmentModel model) {

		// Get all relationships as simple objects
		List<SimpleObject> relationships = new ArrayList<SimpleObject>();
		for (Relationship relationship : Context.getPersonService().getRelationshipsByPerson(patient)) {
			Person person = null;
			String type = null;

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

			if (person.isPatient()) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("patientId", person.getId());
				linkUrl = ui.pageLink(pageRequest.getProviderName(), pageRequest.getPageName(), params);
				linkIcon = ui.resourceLink("kenyaui", "images/glyphs/patient_" + genderCode + ".png");
			}
			else {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("personId", person.getId());
				linkUrl = ui.pageLink(EmrConstants.MODULE_ID, "admin/editAccount", params);
				linkIcon = ui.resourceLink("kenyaui", "images/glyphs/person_" + genderCode + ".png");
			}

			Link link = new Link(kenyaUi.formatPersonName(person), linkUrl, linkIcon);

			relationships.add(SimpleObject.create(
					"relationshipId", relationship.getId(),
					"type", type,
					"personLink", link,
					"startDate", relationship.getStartDate(),
					"endDate", relationship.getEndDate()
			));
		}

		model.addAttribute("patient", patient);
		model.addAttribute("relationships", relationships);
	}
}