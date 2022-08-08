/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.cwc;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.cwc.DateOfVaccineDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.cwc.DateOfVitaminADataDefinition;
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
 * Evaluates a VisitIdDataDefinition to produce a VisitData
 */
@Handler(supports=DateOfVitaminADataDefinition.class, order=50)
public class DateOfVitaminADataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select encounter_id,\n" +
                "concat_ws(\", \", \n" +
                "\tif(VitaminA_6_months != '', STR_TO_DATE(VitaminA_6_months, '%Y/%m/%d'),\"\"), \n" +
                "\tif(VitaminA_1_yr != '', STR_TO_DATE(VitaminA_1_yr, '%Y/%m/%d'),\"\"),\n" +
                "\tif(VitaminA_1_and_half_yr != '', STR_TO_DATE(VitaminA_1_and_half_yr, '%Y/%m/%d'),\"\"),\n" +
                "\tif(VitaminA_2_yr != '', STR_TO_DATE(VitaminA_2_yr, '%Y/%m/%d'),\"\"),\n" +
                "\tif(VitaminA_2_to_5_yr != '', STR_TO_DATE(VitaminA_2_to_5_yr, '%Y/%m/%d'),\"\")\n" +
                "\t) as dates\n" +
                "from kenyaemr_etl.etl_hei_immunization ";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
