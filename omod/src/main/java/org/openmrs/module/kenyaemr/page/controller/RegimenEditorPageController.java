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
import org.openmrs.module.kenyaemr.regimen.RegimenChange;
import org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Controller for regimen editor page
 */
public class RegimenEditorPageController {

	public void controller(@RequestParam("patientId") Patient patient,
						   @RequestParam("category") String category,
						   @RequestParam("returnUrl") String returnUrl,
	                       UiUtils ui,
	                       PageModel model) {

		model.addAttribute("patient", patient);
		model.addAttribute("category", category);
		model.addAttribute("returnUrl", returnUrl);

		Concept masterSet = RegimenManager.getMasterSetConcept(category);
		RegimenChangeHistory history = RegimenChangeHistory.forPatient(patient, masterSet);
		model.addAttribute("history", history);

		RegimenChange lastChange = history.getLastChange();
		Date lastChangeDate =  (lastChange != null) ? lastChange.getDate() : null;
		Date today = OpenmrsUtil.firstSecondOfDay(new Date());
		boolean futureChanges = OpenmrsUtil.compareWithNullAsEarliest(lastChangeDate, today) >= 0;

		model.addAttribute("initialDate", futureChanges ? lastChange.getDate() : today);

		List<SimpleObject> datePresets = new ArrayList<SimpleObject>();
		model.addAttribute("datePresets", datePresets);

		if (category.equals("TB")) {
			datePresets.add(SimpleObject.create("label", "Today", "value", today));

			Calendar calendar = Calendar.getInstance();

			calendar.add(Calendar.MONTH, 1);
			datePresets.add(SimpleObject.create("label", "Today + 1 month", "value", OpenmrsUtil.firstSecondOfDay(calendar.getTime())));
			calendar.add(Calendar.MONTH, 1);
			datePresets.add(SimpleObject.create("label", "Today + 2 months", "value", OpenmrsUtil.firstSecondOfDay(calendar.getTime())));
			calendar.add(Calendar.MONTH, 4);
			datePresets.add(SimpleObject.create("label", "Today + 6 months", "value", OpenmrsUtil.firstSecondOfDay(calendar.getTime())));
		}
	}
}