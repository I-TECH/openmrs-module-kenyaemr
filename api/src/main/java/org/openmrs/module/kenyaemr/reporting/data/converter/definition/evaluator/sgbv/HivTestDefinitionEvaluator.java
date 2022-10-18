/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.sgbv;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.sgbv.HivTestDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.sgbv.P3DataDefinition;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.evaluator.EncounterDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Evaluates Hiv Test DataDefinition
 */
@Handler(supports=HivTestDataDefinition.class, order=50)
public class HivTestDefinitionEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "SELECT a.encounter_id, IF(hiv.program = 'HIV', 'Known Positive',\n" +
                "  IF(hts.final_test_result='Positive', 'Positive',\n" +
                "   IF(hts.final_test_result='Negative', 'Negative', 'Not Done'))) AS hiv_test\n" +
                "   FROM kenyaemr_etl.etl_gbv_screening a\n" +
                "   LEFT JOIN (SELECT patient_id, pg.program\n" +
                "  FROM kenyaemr_etl.etl_patient_program pg WHERE pg.program='HIV' AND pg.date_completed IS NULL) hiv ON a.patient_id = hiv.patient_id\n" +
                "   LEFT JOIN (SELECT ht.patient_id, ht.final_test_result FROM kenyaemr_etl.etl_hts_test ht WHERE ht.test_type=1) hts ON a.patient_id=hts.patient_id;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
