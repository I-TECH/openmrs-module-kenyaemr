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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pnc.PNCNVPAndAZTForBabyGreaterThan6WeeksDataDefinition;
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
 * PNC NVP abd AZT For baby >6 weeks column
 */
@Handler(supports= PNCNVPAndAZTForBabyGreaterThan6WeeksDataDefinition.class, order=50)
public class PNCNVPAndAZTForBabyGreaterThan6WeeksDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select v.encounter_id,\n" +
                "       (case v.baby_azt_dispensed\n" +
                "            when (v.baby_azt_dispensed = 160123 or v.baby_nvp_dispensed = 80586) then \"Yes\"\n" +
                "            when (v.baby_azt_dispensed = 1066 or v.baby_nvp_dispensed = 1066) then \"No\"\n" +
                "            when (v.baby_azt_dispensed = 1175 or v.baby_nvp_dispensed = 1175) then \"N/A\"\n" +
                "            else \"\" end) as azt_nvp_dispensed\n" +
                "from kenyaemr_etl.etl_mch_postnatal_visit v\n" +
                "where date(v.visit_date) between date(:startDate) and date(:endDate) and\n" +
                "      timestampdiff(week, date(v.delivery_date), date(v.visit_date)) > 6;";

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
