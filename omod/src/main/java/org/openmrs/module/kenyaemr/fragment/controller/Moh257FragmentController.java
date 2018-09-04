/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.wrapper.PatientWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * MOH257 fragment
 */
public class Moh257FragmentController {
	
	public void controller(@FragmentParam("patient")
						   Patient patient,
						   FragmentModel model,
						   UiUtils ui,
						   @SpringBean RegimenManager regimenManager) {

		// removing face page from list of available forms
		String[] page1FormUuids = {
				/*HivMetadata._Form.MOH_257_FACE_PAGE,
				HivMetadata._Form.MOH_257_ARV_THERAPY,*/
				HivMetadata._Form.FAMILY_HISTORY
		};

		List<SimpleObject> page1AvailableForms = new ArrayList<SimpleObject>();
		List<Encounter> page1Encounters = new ArrayList<Encounter>();

		PatientWrapper patientWrapper = new PatientWrapper(patient);

		for (String page1FormUuid : page1FormUuids) {
			Form page1Form = MetadataUtils.existing(Form.class, page1FormUuid);
			List<Encounter> formEncounters = patientWrapper.allEncounters(page1Form);

			if (formEncounters.size() == 0) {
				page1AvailableForms.add(ui.simplifyObject(page1Form));
			}
			else {
				page1Encounters.addAll(formEncounters);
			}
		}

		Form moh257VisitForm = MetadataUtils.existing(Form.class, HivMetadata._Form.MOH_257_VISIT_SUMMARY);
		List<Encounter> moh257VisitSummaryEncounters = patientWrapper.allEncounters(moh257VisitForm);
		Collections.reverse(moh257VisitSummaryEncounters);

		model.addAttribute("page1AvailableForms", page1AvailableForms);
		model.addAttribute("page1Encounters", page1Encounters);
		model.addAttribute("page2Form", moh257VisitForm);
		model.addAttribute("page2Encounters", moh257VisitSummaryEncounters);

		Concept masterSet = regimenManager.getMasterSetConcept("ARV");
		RegimenChangeHistory arvHistory = RegimenChangeHistory.forPatient(patient, masterSet);
		model.addAttribute("arvHistory", arvHistory);
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		model.addAttribute("inHivProgram", Context.getProgramWorkflowService().getPatientPrograms(patient, hivProgram, null, null, null, null, true));
	}
}