/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
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
		Date now = new Date();
		boolean futureChanges = OpenmrsUtil.compareWithNullAsEarliest(lastChangeDate, now) >= 0;

		model.addAttribute("initialDate", futureChanges ? lastChangeDate : now);

		try {
			boolean isManager = false;
			for(Role role: Context.getAllRoles(Context.getAuthenticatedUser())) {
				if(role.getName().equals("Manager") || role.getName().equals("System Developer")) {
					isManager = true;
					break;
				}
			}
			model.addAttribute("isManager", isManager);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}