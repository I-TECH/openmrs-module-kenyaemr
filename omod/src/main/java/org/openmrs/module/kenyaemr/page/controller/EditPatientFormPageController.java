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

import java.util.Collections;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Page for editing a per-patient form
 */
@SharedPage
public class EditPatientFormPageController {
	
	public String controller(@RequestParam("appId") String appId,
							 @RequestParam("patientId") Patient patient,
	                         @RequestParam("formUuid") String formUuid,
	                         @RequestParam("returnUrl") String returnUrl) {

			Form form = Context.getFormService().getFormByUuid(formUuid);
			if (form == null) {
				throw new IllegalArgumentException("Cannot find form with uuid = " + formUuid);
			}

			StringBuilder sb = new StringBuilder("redirect:" + EmrConstants.MODULE_ID + "/");

			List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, null, null, Collections.singleton(form), null, null, null, null, false);
			if (encounters.size() == 0) {
				sb.append("enterForm.page?formUuid=" + formUuid);
			} else {
				// in case there are more than one, we pick the last one
				Encounter encounter = encounters.get(encounters.size() - 1);
				sb.append("editForm.page?encounterId=" + encounter.getEncounterId());
			}

			sb.append("&appId=" + appId);
			sb.append("&patientId=" + patient.getId());
			sb.append("&returnUrl=" + java.net.URLEncoder.encode(returnUrl));
			return sb.toString();
		}
}