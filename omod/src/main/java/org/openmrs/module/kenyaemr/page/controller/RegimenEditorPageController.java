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

import java.util.*;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.module.kenyaemr.regimen.RegimenOrderHistory;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for ARV regimen edit screen
 */
public class RegimenEditorPageController {

	public void controller(@RequestParam("patientId") Patient patient, @RequestParam("category") String category,
	                       @RequestParam("returnUrl") String returnUrl,
	                       UiUtils ui,
	                       PageModel model) {

		model.addAttribute("patient", patient);
		model.addAttribute("category", category);
		model.addAttribute("today", OpenmrsUtil.firstSecondOfDay(new Date()));
		model.addAttribute("returnUrl", returnUrl);

		Concept masterSet = RegimenManager.getMasterSetConcept(category);
		RegimenOrderHistory history = RegimenOrderHistory.forPatient(patient, masterSet);
		model.addAttribute("history", history);
	}
}