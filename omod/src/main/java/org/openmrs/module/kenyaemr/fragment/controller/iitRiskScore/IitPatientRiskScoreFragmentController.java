/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.iitRiskScore;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemrml.api.MLinKenyaEMRService;
import org.openmrs.module.kenyaemrml.iit.PatientRiskScore;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * Controller for getting a history of risk score and grouped by the date of evaluation
 */
public class IitPatientRiskScoreFragmentController {

    private static final Logger log = LoggerFactory.getLogger(IitPatientRiskScoreFragmentController.class);

    public void controller(@RequestParam("patientId") Patient patient, PageModel model, UiUtils ui) {
        //Pick latest Patient risk score,evaluationDate and Description
        Date evaluationDate = null;
        String description = "";
        String riskFactor = "";

        PatientRiskScore latestRiskScore = Context.getService(MLinKenyaEMRService.class)
                .getLatestPatientRiskScoreByPatient(Context.getPatientService().getPatient(patient.getPatientId()));
        if (latestRiskScore != null) {
                    evaluationDate = latestRiskScore.getEvaluationDate();
                    description = latestRiskScore.getDescription();
                    riskFactor = latestRiskScore.getRiskFactors();
        }
        model.put("riskScore", latestRiskScore != null ? latestRiskScore.getRiskScore() : "");
        model.put("evaluationDate", evaluationDate != null ? evaluationDate : "");
        model.put("description", description != null ? description : "");
        model.put("riskFactor", riskFactor != null ? riskFactor : "");
    }
}
