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
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

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