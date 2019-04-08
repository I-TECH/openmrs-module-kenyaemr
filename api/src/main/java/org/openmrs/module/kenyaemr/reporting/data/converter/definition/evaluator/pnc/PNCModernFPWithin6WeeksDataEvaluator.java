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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc.PNCModernFPWithin6WeeksDataDefinition;
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
 * PNC Modern FP <=6 weeks column
 */
@Handler(supports= PNCModernFPWithin6WeeksDataDefinition.class, order=50)
public class PNCModernFPWithin6WeeksDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select v.encounter_id,\n" +
                "       (case v.family_planning_method when 160570 then \"Yes\" when 780 then \"Yes\" when 5279 then \"Yes\" when 1359 then \"Yes\"\n" +
                "        when 5275 then \"Yes\" when 136163 then \"Yes\" when 5278 then \"Yes\" when 5277 then \"Yes\"\n" +
                "        when 1472 then \"Yes\" when 190 then \"Yes\" when 1489 then \"Yes\" when 162332 then \"No\" else \"\" end) as Modern_FP_Within_6Weeks\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "       join kenyaemr_etl.etl_mchs_delivery d on d.patient_id = v.patient_id\n" +
                "where timestampdiff(week,d.date_of_delivery,date(v.visit_date))<=6;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
