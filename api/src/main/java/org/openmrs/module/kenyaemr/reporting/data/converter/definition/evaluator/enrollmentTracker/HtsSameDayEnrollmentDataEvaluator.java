/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.enrollmentTracker;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.enrollmentTracker.HtsDateConfirmedPositiveDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.enrollmentTracker.HtsSameDayEnrollmentDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Evaluates if linkage for a client was done on the same day and testing
 */
@Handler(supports=HtsSameDayEnrollmentDataDefinition.class, order=50)
public class HtsSameDayEnrollmentDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select patient_id, if(linked is not null or enrolled is not null, \"Yes\",\"No\") sameDayEnrollment\n" +
                "from (\n" +
                "select t.patient_id, max(l.patient_id) linked, max(e.patient_id) enrolled\n" +
                "from kenyaemr_etl.etl_hts_test t\n" +
                "left outer join kenyaemr_etl.etl_hts_referral_and_linkage l on l.patient_id = t.patient_id and date(t.visit_date)=date(l.visit_date) and l.tracing_status=\"Contacted and linked\"\n" +
                "left outer join kenyaemr_etl.etl_hiv_enrollment e on e.patient_id = t.patient_id and date(t.visit_date)=date(e.visit_date)\n" +
                "where t.voided=0\n" +
                "group by t.patient_id\n" +
                ") l ;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}