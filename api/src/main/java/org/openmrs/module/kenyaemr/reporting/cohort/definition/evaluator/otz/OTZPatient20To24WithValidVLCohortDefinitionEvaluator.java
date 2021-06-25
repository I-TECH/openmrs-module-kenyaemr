/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator.otz;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.otz.OTZPatient20To24WithValidVLonReportingPeriodCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.otz.OTZPatientWithValidVLonReportingPeriodCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.otz.ETLOtzCohortLibrary;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Evaluates a OnPatientsWithValidVLDataDefinition
 */
@Handler(supports = {OTZPatient20To24WithValidVLonReportingPeriodCohortDefinition.class})
public class OTZPatient20To24WithValidVLCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    @Autowired
    private ETLOtzCohortLibrary otzCohortLibrary;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

        OTZPatient20To24WithValidVLonReportingPeriodCohortDefinition definition = (OTZPatient20To24WithValidVLonReportingPeriodCohortDefinition) cohortDefinition;
        Integer month = definition.getMonth();
        CohortDefinition cd = otzCohortLibrary.patient20To24WithValidVL();

        Calendar calendar = Calendar.getInstance();
        int thisMonth = calendar.get(calendar.MONTH);

        Map<String, Date> dateMap = EmrReportingUtils.getReportDates(thisMonth - 1);
        Date startDate = dateMap.get("startDate");
        Date endDate = dateMap.get("endDate");

        context.addParameterValue("startDate", startDate);
        context.addParameterValue("endDate", endDate);
        context.addParameterValue("month", month);

        Cohort patients20To24WithValidVL = Context.getService(CohortDefinitionService.class).evaluate(cd, context);


        return new EvaluatedCohort(patients20To24WithValidVL, definition, context);
    }
}
