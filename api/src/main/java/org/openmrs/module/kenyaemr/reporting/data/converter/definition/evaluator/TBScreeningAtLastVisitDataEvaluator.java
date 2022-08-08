/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.TBScreeningAtLastVisitDataDefinition;
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
 * Evaluates a patient's tb screening at last visit
 */
@Handler(supports=TBScreeningAtLastVisitDataDefinition.class, order=50)
public class TBScreeningAtLastVisitDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "SELECT patient_id, mid(max(concat(visit_date, lastTBStatus)), 11) as lastTBStatus\n" +
                "    from\n" +
                "(\n" +
                "    SELECT\n" +
                "        f.patient_id,\n" +
                "        f.visit_date,\n" +
                "        coalesce(f.tb_status, s.resulting_tb_status) AS lastTBStatus\n" +
                "    FROM kenyaemr_etl.etl_patient_hiv_followup f\n" +
                "        LEFT OUTER JOIN kenyaemr_etl.etl_tb_screening s\n" +
                "            ON s.patient_id = f.patient_id AND date(s.visit_date) = date(f.visit_date)\n" +
                "    GROUP BY f.patient_id\n" +
                ") v\n" +
                "GROUP BY patient_id;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}