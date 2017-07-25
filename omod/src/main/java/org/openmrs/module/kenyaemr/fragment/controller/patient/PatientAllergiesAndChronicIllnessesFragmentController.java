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
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		Concept allergyQ = Context.getConceptService().getConcept(160643);
		Concept chronicIllnessQ = Context.getConceptService().getConcept(1284);
		List<Form> formsCollectingAllergies = Arrays.asList(
				Context.getFormService().getFormByUuid("47814d87-2e53-45b1-8d05-ac2e944db64c"),
				Context.getFormService().getFormByUuid("22c68f86-bbf0-49ba-b2d1-23fa7ccf0259")
		);

		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, null, null, formsCollectingAllergies, null, null,false);
		List<Person> patients = new ArrayList<Person>();
		patients.add(patient);

		List<Concept> question = new ArrayList<Concept>();
		question.add(allergyQ);

		List<Obs> allergies = obsService.getObservations(
				patients,
				encounters,
				question,
				null,
				null,
				null,
				null,
				null,
				null,
				null, null, false);

		List<SimpleObject> allergyList = new ArrayList<SimpleObject>();
		List<SimpleObject> illnessList = new ArrayList<SimpleObject>();
		Concept noConcept = Context.getConceptService().getConcept(1066);
		int allergyCounter = 0;
		for(Obs obs: allergies) {
			allergyCounter++;
			String allergen = obs.getValueCoded().equals(noConcept)? "None": allergyCounter + "." + obs.getValueCoded().getName().getName();
			allergyList.add(SimpleObject.create(
				"allergen", allergen
			));
		}

		List<Obs> chronicIllnesses = obsService.getObservationsByPersonAndConcept(patient, new Concept(1284));
		int illnessCounter = 0;
		for(Obs o: chronicIllnesses) {
			if(o.getValueCoded() != null && o.getValueCoded() == noConcept) {
				illnessCounter++;
				String illness = illnessCounter + "." + o.getValueCoded().getName().getName();
				illnessList.add(SimpleObject.create(
						"illness", illness
				));
			}
		}

		model.addAttribute("patient", patient);
		model.addAttribute("allergies", allergyList);
		model.addAttribute("illnesses", illnessList);
	}
}