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

import java.util.Collections;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.module.kenyaemr.KenyaEmrConstants;
import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 */
/*@SharedPage({EmrWebConstants.APP_REGISTRATION, EmrWebConstants.APP_INTAKE, EmrWebConstants.APP_MEDICAL_ENCOUNTER, EmrWebConstants.APP_MEDICAL_CHART})*/
public class EditProgramHtmlFormPageController {

	public String controller(@RequestParam("appId") String appId,
							 @RequestParam("patientId") Patient patient,
							 @RequestParam("patientProgramId") PatientProgram enrollment,
							 @RequestParam("formUuid") String formUuid,
							 @RequestParam("returnUrl") String returnUrl) {

		Form form = Context.getFormService().getFormByUuid(formUuid);
		if (form == null) {
			throw new IllegalArgumentException("Cannot find form with uuid = " + formUuid);
		}

		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, enrollment.getDateEnrolled(), enrollment.getDateCompleted(), Collections.singleton(form), null, null, null, null, false);

		if (encounters.size() == 0) {
			throw new RuntimeException("Cannot find the encounter for this registration");
		}
		else {
			// in case there are more than one, we pick the last one
			Encounter encounter = encounters.get(encounters.size() - 1);
			return "redirect:" + KenyaEmrConstants.MODULE_ID + "/editHtmlForm.page?appId=" + appId + "&patientId=" + patient.getId() + "&encounterId=" + encounter.getEncounterId() + "&returnUrl=" + java.net.URLEncoder.encode(returnUrl);
		}
	}
}