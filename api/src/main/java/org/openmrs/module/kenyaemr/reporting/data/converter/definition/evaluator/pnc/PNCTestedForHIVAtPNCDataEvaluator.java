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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc.PNCTestedForHIVAtPNCDataDefinition;
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
 * PNC tested for HIV at PNC column
 */
@Handler(supports= PNCTestedForHIVAtPNCDataDefinition.class, order=50)
public class PNCTestedForHIVAtPNCDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select a.encounter_id, if(a.tested_at_pnc is not null, 'Yes', 'No') as tested_at_pnc\n" +
                "from (select v.patient_id,\n" +
                "             v.encounter_id,\n" +
                "             coalesce(nullif(v.final_test_result,''), nullif(t.final_test_result,'')) as tested_at_pnc\n" +
                "      from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "               left join (select t.encounter_id, t.patient_id, t.visit_date, t.hts_entry_point, t.final_test_result\n" +
                "                          from kenyaemr_etl.etl_hts_test t\n" +
                "                            where date(t.visit_date) between date(:startDate) and date(:endDate)) t\n" +
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
