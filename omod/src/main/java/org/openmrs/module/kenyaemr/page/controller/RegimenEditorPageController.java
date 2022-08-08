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

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.util.EncounterBasedRegimenUtils;
import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Controller for regimen editor page
 */
@SharedPage({EmrConstants.APP_CLINICIAN, EmrConstants.APP_CHART})
public class RegimenEditorPageController {

	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");
	public void controller(@RequestParam("category") String category,
						   @RequestParam("returnUrl") String returnUrl,
						   PageModel model,
						   @SpringBean RegimenManager regimenManager) {

		Patient patient = (Patient) model.getAttribute(EmrWebConstants.MODEL_ATTR_CURRENT_PATIENT);

		model.addAttribute("category", category);
		model.addAttribute("returnUrl", returnUrl);

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
		List<SimpleObject> obshistory = EncounterBasedRegimenUtils.getRegimenHistoryFromObservations(patient, category);
		model.put("regimenFromObs", obshistory);
		Encounter lastEnc = EncounterBasedRegimenUtils.getLastEncounterForCategory(patient, category);

		SimpleObject lastEncDetails = null;
		String event = null;
		String ARV_TREATMENT_PLAN_EVENT = "1255AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

		if (lastEnc != null) {
			Date latest = null;
			List<Date> dates = new ArrayList<Date>();
			for(Obs obs:lastEnc.getObs()) {
				dates.add(obs.getObsDatetime());
				latest = Collections.max(dates);
			}

			for(Obs obs:lastEnc.getObs()) {
				if(obs.getConcept().getUuid().equals(ARV_TREATMENT_PLAN_EVENT) && obs.getObsDatetime().equals(latest)) {
					event =obs.getValueCoded() != null ?  obs.getValueCoded().getName().getName() : "";
				}

			}
			lastEncDetails = EncounterBasedRegimenUtils.buildRegimenChangeObject(lastEnc.getObs(), lastEnc);
		}
		model.put("lastEnc", lastEncDetails);
		model.put("regimenEvent", event);
	}


}