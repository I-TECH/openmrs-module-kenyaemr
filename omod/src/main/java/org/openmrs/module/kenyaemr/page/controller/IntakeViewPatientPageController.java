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

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * View patient page for intake app
 */
@AppPage(EmrWebConstants.APP_INTAKE)
public class IntakeViewPatientPageController {

	public void controller(@RequestParam("patientId") Patient patient,
	                       @RequestParam(value="visitId", required=false) Visit visit,
	                       PageModel model) {

		model.addAttribute("patient", patient);
		model.addAttribute("person", patient);
		
		List<Visit> activeVisits = Context.getVisitService().getActiveVisitsByPatient(patient);

		model.addAttribute("activeVisits", activeVisits);
		
		if (visit == null && activeVisits.size() > 0) {
			visit = activeVisits.get(0);
		}
		
		model.addAttribute("visit", visit);

		model.addAttribute("hivProgram", Metadata.getProgram(Metadata.HIV_PROGRAM));
		model.addAttribute("tbProgram", Metadata.getProgram(Metadata.TB_PROGRAM));
	}
}