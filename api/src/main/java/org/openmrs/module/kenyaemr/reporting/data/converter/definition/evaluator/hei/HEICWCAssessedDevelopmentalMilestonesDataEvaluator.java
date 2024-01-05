/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.hei;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEICWCAssessedDevelopmentalMilestonesDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEICWCDewormedDataDefinition;
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
 * Evaluates Current HEI Assessed Developmental Milestones Data Definition
 */
@Handler(supports=HEICWCAssessedDevelopmentalMilestonesDataDefinition.class, order=50)
public class HEICWCAssessedDevelopmentalMilestonesDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select encounter_id,\n" +
                "  if(coalesce (social_smile_milestone,\n" +
                "               head_control_milestone,\n" +
                "               response_to_sound_milestone,\n" +
                "               hand_extension_milestone,\n" +
                "               sitting_milestone,\n" +
                "               walking_milestone,\n" +
                "               standing_milestone,\n" +
                "               talking_milestone) is not null,\"Yes\",\"No\") as assessed_for_development_milestone\n" +
                "from kenyaemr_etl.etl_hei_follow_up_visit;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
