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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc.PNCTestTwoResultsDataDefinition;
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
 * PNC test 2 results column
 */
@Handler(supports= PNCTestTwoResultsDataDefinition.class, order=50)
public class PNCTestTwoResultsDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "\n" +
                "select v.encounter_id,\n" +
                "       CONCAT_WS('\\r\\n', v.test_2_kit_name, v.test_2_kit_lot_no, v.test_2_kit_expiry,\n" +
                "                 v.test_2_result) as Test_two_results\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "where date(v.visit_date) between date(:startDate) and date(:endDate)\n" +
                "union\n" +
                "select ht.encounter_id,\n" +
                "       CONCAT_WS('\\r\\n', ht.test_2_kit_name, ht.test_2_kit_lot_no, ht.test_2_kit_expiry,\n" +
                "                 ht.test_2_result) as Test_two_results\n" +
                "from kenyaemr_etl.etl_hts_test ht\n" +
                "         join kenyaemr_etl.etl_mch_antenatal_visit anc\n" +
                "              on anc.patient_id = ht.patient_id and anc.visit_date = ht.visit_date;";

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
