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

import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for editing an existing HTML form
 */
/*@SharedPage({EmrWebConstants.APP_REGISTRATION, EmrWebConstants.APP_INTAKE, EmrWebConstants.APP_MEDICAL_ENCOUNTER, EmrWebConstants.APP_MEDICAL_CHART})*/
public class EditHtmlFormPageController {
	
	public void controller(@RequestParam("encounterId") Encounter encounter,
	                       @RequestParam("returnUrl") String returnUrl,
	                       PageModel model) {
		model.addAttribute("encounter", encounter);
		model.addAttribute("patient", encounter.getPatient());
		model.addAttribute("person", encounter.getPatient());
		model.addAttribute("visit", encounter.getVisit());
		model.addAttribute("returnUrl", returnUrl);
	}
}