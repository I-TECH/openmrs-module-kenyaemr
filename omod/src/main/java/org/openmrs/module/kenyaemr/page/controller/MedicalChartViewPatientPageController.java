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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.AppUiUtil;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.session.Session;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Viewing a patient's record, in the medicalChart app
 */
public class MedicalChartViewPatientPageController {

	public void controller(@RequestParam("patientId") Patient patient,
	                       @RequestParam(required = false, value = "visitId") Visit visit,
	                       @RequestParam(required = false, value = "formUuid") String formUuid,
	                       @RequestParam(required = false, value = "patientProgramId") PatientProgram pp,
	                       PageModel model,
	                       UiUtils ui,
	                       Session session) {

		if ("".equals(formUuid)) {
			formUuid = null;
		}

		AppUiUtil.startApp("kenyaemr.medicalChart", session);
		
		recentlyViewed(patient, session);
		
		model.addAttribute("patient", patient);
		model.addAttribute("person", patient);
		
		List<SimpleObject> oneTimeForms = new ArrayList<SimpleObject>();
		oneTimeForms.add(SimpleObject.create("label", "Family History", "formUuid", MetadataConstants.FAMILY_HISTORY_FORM_UUID));
		if ("F".equals(patient.getGender())) {
			oneTimeForms.add(SimpleObject.create("label", "Obstetric History", "formUuid", MetadataConstants.OBSTETRIC_HISTORY_FORM_UUID));
		}
		model.addAttribute("oneTimeForms", oneTimeForms);

		model.addAttribute("programs", Context.getProgramWorkflowService().getPatientPrograms(patient, null, null, null, null, null, false));
		model.addAttribute("program", pp);
		
		model.addAttribute("visits", Context.getVisitService().getVisitsByPatient(patient));
		model.addAttribute("visit", visit);
		
		Form form = null;
		Encounter encounter = null;
		
		String selection = "overview";
		
		if (visit != null) {
			selection = "visit-" + visit.getVisitId();
		}
		else if (formUuid != null) {
			selection = "form-" + formUuid;
			
			form = Context.getFormService().getFormByUuid(formUuid);
			List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, null, null, Collections.singleton(form), null, null, null, null, false);
			encounter = encounters.size() > 0 ? encounters.get(0) : null;
		}
		else if (pp != null) {
			selection = "program-" + pp.getPatientProgramId();
		}

		model.addAttribute("form", form);
		model.addAttribute("encounter", encounter);
		model.addAttribute("selection", selection);
	}

	/**
     * Remember that this patient was just viewed
     */
    private void recentlyViewed(Patient patient, Session session) {
	    LinkedList<Integer> recent = session.getAttribute("kenyaemr.medicalChart.recentlyViewedPatients", LinkedList.class);
	    if (recent == null) {
	    	recent = new LinkedList<Integer>();
	    	session.setAttribute("kenyaemr.medicalChart.recentlyViewedPatients", recent);
	    }
	    recent.removeFirstOccurrence(patient.getPatientId());
	    recent.add(0, patient.getPatientId());
	    while (recent.size() > 10)
	    	recent.removeLast();
    }
	
}
