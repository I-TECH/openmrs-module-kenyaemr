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

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for entering a new HTML form
 */
/*@SharedPage({EmrWebConstants.APP_REGISTRATION, EmrWebConstants.APP_INTAKE, EmrWebConstants.APP_MEDICAL_ENCOUNTER, EmrWebConstants.APP_MEDICAL_CHART})*/
public class EnterHtmlFormPageController {

	public void controller(@RequestParam("patientId") Patient patient,
	                       @RequestParam(value = "formUuid", required = false) String formUuid,
	                       @RequestParam(value = "htmlFormId", required = false) String htmlFormId,
						   @RequestParam(value = "visitId", required = false) Visit visit,
	                       @RequestParam("returnUrl") String returnUrl,
	                       PageModel model) {

		model.addAttribute("formUuid", formUuid);
		model.addAttribute("htmlFormId", htmlFormId);
		model.addAttribute("patient", patient);
		model.addAttribute("person", patient);
		model.addAttribute("visit", visit);
		model.addAttribute("returnUrl", returnUrl);
	}
}