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

import java.util.Date;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.Visit;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.AppUiUtil;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.session.Session;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 */
public class RegistrationViewPatientPageController {
	
	public void controller(@RequestParam("patientId") Patient patient,
	                       @RequestParam(value="visitId", required=false) Visit visit,
	                       PageModel model,
	                       Session session) {
		
		PatientService ps = Context.getPatientService();
		
		AppUiUtil.startApp("kenyaemr.registration", session);

		model.addAttribute("MC", new MetadataConstants());		
		model.addAttribute("patient", patient);
		model.addAttribute("person", patient);
				
		List<Visit> activeVisits = Context.getVisitService().getActiveVisitsByPatient(patient);

		model.addAttribute("activeVisits", activeVisits);
		
		if (visit == null && activeVisits.size() > 0) {
			visit = activeVisits.get(0);
		}
		
		model.addAttribute("visit", visit);
		
		if (activeVisits.size() == 0) {
			Visit newVisit = new Visit();
			newVisit.setPatient(patient);
			newVisit.setStartDatetime(new Date());
			model.addAttribute("newCurrentVisit", newVisit);
		}
	}
	
}
