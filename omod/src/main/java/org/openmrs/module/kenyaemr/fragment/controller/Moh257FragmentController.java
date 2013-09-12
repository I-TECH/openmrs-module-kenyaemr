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

package org.openmrs.module.kenyaemr.fragment.controller;

import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Moh257FragmentController {
	
	public void controller(@FragmentParam("patient")
						   Patient patient,
						   FragmentModel model,
						   UiUtils ui,
						   @SpringBean RegimenManager regimenManager) {

		String[] page1FormUuids = { Metadata.Form.MOH_257_FACE_PAGE };
		//String[] page1FormUuids = { Metadata.Form.FAMILY_HISTORY, Metadata.Form.HIV_ENROLLMENT };

		List<SimpleObject> page1AvailableForms = new ArrayList<SimpleObject>();
		List<Encounter> page1Encounters = new ArrayList<Encounter>();

		for (String page1FormUuid : page1FormUuids) {
			Form page1Form = MetadataUtils.getForm(page1FormUuid);
			List<Encounter> formEncounters = getPatientEncounterByForm(patient, page1Form);

			if (formEncounters.size() == 0) {
				page1AvailableForms.add(ui.simplifyObject(page1Form));
			}
			else {
				page1Encounters.addAll(formEncounters);
			}
		}

		Form moh257VisitForm = MetadataUtils.getForm(Metadata.Form.MOH_257_VISIT_SUMMARY);
		List<Encounter> moh257VisitSummaryEncounters = getPatientEncounterByForm(patient, moh257VisitForm);

		model.addAttribute("page1AvailableForms", page1AvailableForms);
		model.addAttribute("page1Encounters", page1Encounters);
		model.addAttribute("page2Form", moh257VisitForm);
		model.addAttribute("page2Encounters", moh257VisitSummaryEncounters);

		Concept masterSet = regimenManager.getMasterSetConcept("ARV");
		RegimenChangeHistory arvHistory = RegimenChangeHistory.forPatient(patient, masterSet);
		model.addAttribute("arvHistory", arvHistory);
	}

	/**
	 * Convenience method to get encounters from the given form
	 * @param patient the patient
	 * @param form the form
	 * @return the encounters
	 */
	private static List<Encounter> getPatientEncounterByForm(Patient patient, Form form) {
		return Context.getEncounterService().getEncounters(patient, null, null, null, Collections.singleton(form), null, null, null, null, false);
	}
}