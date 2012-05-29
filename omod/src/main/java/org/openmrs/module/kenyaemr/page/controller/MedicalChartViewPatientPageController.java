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

import java.util.LinkedList;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.AppUiUtil;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.session.Session;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Viewing a patient's record, in the medicalChart app
 */
public class MedicalChartViewPatientPageController {

	public void controller(@RequestParam("patientId") Patient patient,
	                       PageModel model,
	                       Session session) {

		AppUiUtil.startApp("kenyaemr.medicalChart", session);
		
		recentlyViewed(patient, session);
		
		model.addAttribute("patient", patient);
		model.addAttribute("person", patient);
		
		model.addAttribute("visits", Context.getVisitService().getVisitsByPatient(patient));
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
