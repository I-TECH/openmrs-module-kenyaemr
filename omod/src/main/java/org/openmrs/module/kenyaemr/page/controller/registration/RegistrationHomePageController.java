/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller.registration;

import org.openmrs.Patient;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * Homepage for the registration app
 */
@AppPage(EmrConstants.APP_REGISTRATION)
public class RegistrationHomePageController {

	public String controller(UiUtils ui,
							 @RequestParam(required = false, value = "scheduleDate") Date scheduleDate,
	                         PageModel model) {

		Patient patient = (Patient) model.getAttribute(EmrWebConstants.MODEL_ATTR_CURRENT_PATIENT);

		// Get the date for schedule view
		if (scheduleDate == null) {
			scheduleDate = new Date();
		}
		scheduleDate = DateUtil.getStartOfDay(scheduleDate);
		model.addAttribute("scheduleDate", scheduleDate);

		if (patient != null) {
			return "redirect:" + ui.pageLink(EmrConstants.MODULE_ID, "registration/registrationViewPatient", SimpleObject.create("patientId", patient.getId()));
		} else {
			return null;
		}
	}
}