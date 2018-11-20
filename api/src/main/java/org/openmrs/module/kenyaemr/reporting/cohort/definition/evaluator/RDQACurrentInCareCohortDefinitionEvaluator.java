/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.RDQACurrentInCareCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731.ETLMoh731CohortLibrary;
import org.openmrs.module.kenyaemr.reporting.library.moh731.Moh731CohortLibrary;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Evaluator for patients eligible for RDQA
 */
@Handler(supports = {RDQACurrentInCareCohortDefinition.class})
public class RDQACurrentInCareCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());
    @Autowired
    private ETLMoh731CohortLibrary moh731Cohorts;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

        RDQACurrentInCareCohortDefinition definition = (RDQACurrentInCareCohortDefinition) cohortDefinition;
        CohortDefinition cd = moh731Cohorts.currentlyInCare();//, "onDate=${endDate}"

        Calendar calendar = Calendar.getInstance();
        int thisMonth = calendar.get(calendar.MONTH);

        Map<String, Date> dateMap = EmrReportingUtils.getReportDates(thisMonth - 1);
        Date startDate = dateMap.get("startDate");
        Date endDate = dateMap.get("endDate");

        context.addParameterValue("startDate", startDate);
        context.addParameterValue("endDate", endDate);

        Cohort currentInCare = Context.getService(CohortDefinitionService.class).evaluate(cd, context);

        return new EvaluatedCohort(currentInCare, definition, context);
    }


}
