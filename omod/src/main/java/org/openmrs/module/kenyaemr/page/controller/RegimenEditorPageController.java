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

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.module.kenyaemr.regimen.RegimenChange;
import org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * Controller for regimen editor page
 */
@SharedPage({EmrConstants.APP_CLINICIAN, EmrConstants.APP_CHART})
public class RegimenEditorPageController {

	public void controller(@RequestParam("category") String category,
						   @RequestParam("returnUrl") String returnUrl,
						   PageModel model,
						   @SpringBean RegimenManager regimenManager) {

		Patient patient = (Patient) model.getAttribute(EmrWebConstants.MODEL_ATTR_CURRENT_PATIENT);

		model.addAttribute("category", category);
		model.addAttribute("returnUrl", returnUrl);

		Concept masterSet = regimenManager.getMasterSetConcept(category);
		RegimenChangeHistory history = RegimenChangeHistory.forPatient(patient, masterSet);
		model.addAttribute("history", history);

		RegimenChange lastChange = history.getLastChange();
		Date lastChangeDate =  (lastChange != null) ? lastChange.getDate() : null;
		Date today = OpenmrsUtil.firstSecondOfDay(new Date());
		boolean futureChanges = OpenmrsUtil.compareWithNullAsEarliest(lastChangeDate, today) >= 0;

		model.addAttribute("initialDate", futureChanges ? lastChange.getDate() : today);
	}
}