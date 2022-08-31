/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.pama;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pama.PamaCareGiverNameDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pama.PamaCareGiverStatusDataDefinition;
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
 * Evaluates a PersonDataDefinition
 */
@Handler(supports= PamaCareGiverStatusDataDefinition.class, order=50)
public class PamaCareGiverStatusDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select r.person_b,\n" +
                "  if(ht.final_test_result='Positive' or d.unique_patient_no is not null,'Positive',\n" +
                "     if(ht.final_test_result='Negative','Negative',\n" +
                "        if(ht.final_test_result='Unknown','Unknown',''))) as care_giver_status\n" +
                "from kenyaemr_etl.etl_patient_demographics d\n" +
                "  inner join openmrs.relationship r on d.patient_id = r.person_a\n" +
                "  inner join openmrs.relationship_type t on r.relationship = t.relationship_type_id and t.uuid in ('8d91a210-c2cc-11de-8d13-0010c6dffd0f','5f115f62-68b7-11e3-94ee-6bef9086de92')\n" +
                "  left outer join (select mid(max(concat(t.visit_date, t.patient_id)),11) as patient_id,t.final_test_result from kenyaemr_etl.etl_hts_test t  group by t.patient_id) ht on ht.patient_id = d.patient_id\n" +
                "  GROUP BY r.person_b;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}