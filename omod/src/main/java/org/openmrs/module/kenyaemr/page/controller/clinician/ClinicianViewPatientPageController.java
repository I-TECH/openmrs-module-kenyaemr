/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller.clinician;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemrml.api.MLinKenyaEMRService;
import org.openmrs.module.kenyaemrml.iit.PatientRiskScore;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * View patient page for clinician app
 */
@AppPage(EmrConstants.APP_CLINICIAN)
public class ClinicianViewPatientPageController {

	public void controller(@RequestParam("patientId") Patient patient, PageModel model, UiUtils ui) {
		//Pick latest Patient risk score

		PatientRiskScore latestRiskScore = Context.getService(MLinKenyaEMRService.class)
				.getLatestPatientRiskScoreByPatient(Context.getPatientService().getPatient(patient.getPatientId()));
		model.put("riskScore", latestRiskScore != null ? latestRiskScore.getRiskScore() : "");

	}
}