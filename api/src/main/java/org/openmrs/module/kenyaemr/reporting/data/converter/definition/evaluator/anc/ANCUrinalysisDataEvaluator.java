/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.anc;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.anc.ANCCountyDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.anc.ANCUrinalysisDataDefinition;
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
 * Evaluates a Visit Number Data Definition to produce a Visit Number
 */
@Handler(supports= ANCUrinalysisDataDefinition.class, order=50)
public class ANCUrinalysisDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select v.encounter_id,if(v.urine_microscopy is not null or v.urinary_albumin is not null or v.glucose_measurement is not null\n" +
                "                            or v.urine_ph is not null or v.urine_gravity is not null or v.urine_nitrite_test is not null\n" +
                "                            or v.urine_dipstick_for_blood is not null or v.urine_leukocyte_esterace_test is not null or v.urinary_ketone is not null\n" +
                "                            or v.urine_bile_pigment_test is not null or v.urine_bile_salt_test is not null or v.urine_colour is not null or v.urine_turbidity is not null,'Y','N') as urinalysis from kenyaemr_etl.etl_mch_antenatal_visit v;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
