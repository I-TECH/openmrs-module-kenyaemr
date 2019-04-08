/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator.pmtct.maternity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.pmtct.maternity.InfantARVProphylaxisAtMaternityCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.library.ETLReports.MOH731Greencard.ETLMoh731GreenCardCohortLibrary;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Evaluator for infant ARV Prophylaxis at maternity
 */
@Handler(supports = {InfantARVProphylaxisAtMaternityCohortDefinition.class})
public class MaternityInfantARVProphylaxisCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
    @Autowired
    EvaluationService evaluationService;
    private final Log log = LogFactory.getLog(this.getClass());
    @Autowired
    private ETLMoh731GreenCardCohortLibrary moh731GreenCardCohortLibrary;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

        InfantARVProphylaxisAtMaternityCohortDefinition definition = (InfantARVProphylaxisAtMaternityCohortDefinition) cohortDefinition;
        if (definition == null)
            return null;

        String query = "select distinct ld.patient_id\n" +
                "                from kenyaemr_etl.etl_mchs_delivery ld\n" +
                "                  left outer join kenyaemr_etl.etl_mch_antenatal_visit v on v.patient_id=ld.patient_id\n" +
                "                 left outer join kenyaemr_etl.etl_mch_postnatal_visit p on p.patient_id=ld.patient_id\n" +
                "                where (ld.baby_nvp_dispensed = 160123 or ld.baby_azt_dispensed = 160123) and\n" +
                "                      (p.baby_nvp_dispensed != 160123 or p.baby_azt_dispensed != 160123) and\n" +
                "                      (v.baby_nvp_dispensed != 160123 or v.baby_azt_dispensed != 160123);";

        Cohort newCohort = new Cohort();
        SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.append(query);
        Date startDate = (Date)context.getParameterValue("startDate");
        Date endDate = (Date)context.getParameterValue("endDate");
        builder.addParameter("endDate", endDate);
        builder.addParameter("startDate", startDate);
        List<Integer> ptIds = evaluationService.evaluateToList(builder, Integer.class, context);

        newCohort.setMemberIds(new HashSet<Integer>(ptIds));


        return new EvaluatedCohort(newCohort, definition, context);
    }

}