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

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.api.ObsService;
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
import java.util.Set;

/**
 * Controller for patient Allergies and chronic illnesses summary panel
 */
public class PatientAllergiesAndChronicIllnessesFragmentController {

	public void controller(@FragmentParam(value="patient") Patient patient,
						   @SpringBean KenyaUiUtils kenyaUi,
						   PageRequest pageRequest,
						   UiUtils ui,
						   FragmentModel model) {

		// get list of recorded allergies
		ObsService obsService = Context.getObsService();
		Concept concept = Context.getConceptService().getConcept(160643);
		List<Obs> allergies = obsService.getObservationsByPersonAndConcept(patient, concept);

		List<SimpleObject> allergyList = new ArrayList<SimpleObject>();
		for(Obs obs: allergies) {
			String allergen = obs.getValueCoded().getName().toString();
			allergyList.add(SimpleObject.create(
				"allergen", allergen
			));
		}
		System.out.println(allergies);
		model.addAttribute("patient", patient);
		model.addAttribute("allergies", allergyList);
	}
}