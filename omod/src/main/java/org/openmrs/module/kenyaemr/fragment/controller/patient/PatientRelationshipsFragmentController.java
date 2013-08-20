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

package org.openmrs.module.kenyaemr.fragment.controller.patient;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.api.context.Context;
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
			String rel = null;

			if (patient.equals(relationship.getPersonA())) {
				person = relationship.getPersonB();
				rel = relationship.getRelationshipType().getbIsToA();
			}
			else if (patient.equals(relationship.getPersonB())) {
				person = relationship.getPersonA();
				rel = relationship.getRelationshipType().getaIsToB();
			}

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("patientId", person.getId());
			String linkUrl = ui.pageLink(pageRequest.getProviderName(), pageRequest.getPageName(), params);
			Link link = new Link(kenyaUi.formatPersonName(person), linkUrl, null);

			relationships.add(SimpleObject.create("relationship", rel, "link", link));
		}

		model.addAttribute("patient", patient);
		model.addAttribute("relationships", relationships);
	}
}