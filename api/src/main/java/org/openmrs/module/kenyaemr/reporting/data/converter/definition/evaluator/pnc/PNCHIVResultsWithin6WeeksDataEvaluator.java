/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.pnc;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc.PNCHIVResultsWithin6WeeksDataDefinition;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.evaluator.EncounterDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

/**
 * PNC HIV Results <=6 weeks column
 */
@Handler(supports= PNCHIVResultsWithin6WeeksDataDefinition.class, order=50)
public class PNCHIVResultsWithin6WeeksDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select a.encounter_id,\n" +
                "       if(timestampdiff(WEEK, a.date_of_delivery, a.visit_date) <= 6 and tested_at_pnc in ('Positive', 'Negative'), 'Yes',\n" +
                "          'No') as tested_within_6_weeks\n" +
                "from (select v.patient_id,\n" +
                "             v.encounter_id,\n" +
                "             coalesce(v.visit_date, t.visit_date)               as visit_date,\n" +
                "             coalesce(d.date_of_delivery, v.delivery_date)      as date_of_delivery,\n" +
                "             coalesce(v.final_test_result, t.final_test_result) as tested_at_pnc\n" +
                "      from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "               left join (select d.patient_id,\n" +
                "                                 mid(max(concat(d.visit_date, date(d.date_of_delivery))), 11) as date_of_delivery\n" +
                "                          from kenyaemr_etl.etl_mchs_delivery d\n" +
                "                          where d.visit_date <= date(:endDate)\n" +
                "                          group by d.patient_id) d\n" +
                "                         on v.patient_id = d.patient_id\n" +
                "               left join (select t.encounter_id, t.patient_id, t.visit_date, t.hts_entry_point, t.final_test_result\n" +
                "                          from kenyaemr_etl.etl_hts_test t\n" +
                "                          where date(t.visit_date) between date(:startDate) and date(:endDate)) t\n" +
                "                         on v.patient_id = t.patient_id and v.visit_date = t.visit_date\n" +
                "      where date(v.visit_date) between date(:startDate) and date(:endDate)) a;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Date startDate = (Date)context.getParameterValue("startDate");
        Date endDate = (Date)context.getParameterValue("endDate");
        queryBuilder.addParameter("endDate", endDate);
        queryBuilder.addParameter("startDate", startDate);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
