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
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 */
public class EnterHtmlFormPageController {

	public void controller(@RequestParam("patientId") Patient patient,
	                       @RequestParam(required = false, value = "formUuid") String formUuid,
	                       @RequestParam(required = false, value = "htmlFormId") String htmlFormId,
	                       @RequestParam("returnUrl") String returnUrl,
	                       PageModel model) {
		model.addAttribute("formUuid", formUuid);
		model.addAttribute("htmlFormId", htmlFormId);
		model.addAttribute("patient", patient);
		model.addAttribute("person", patient);
		model.addAttribute("returnUrl", returnUrl);
	}
	
}
