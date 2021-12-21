/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.defaulterTracing;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.FinalOutcomeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.ReasonForMissedAppointmentDataDefinition;
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
 * Evaluates reason for missed appointment to produce a VisitData
 */
@Handler(supports=ReasonForMissedAppointmentDataDefinition.class, order=50)
public class ReasonForMissedAppointmentDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select encounter_id,\n" +
                "  (case reason_for_missed_appointment  when 165609 then 'Client has covid-19 infection'\n" +
                "                     when 165610 then 'COVID-19 restrictions'\n" +
                "                     when 164407 then 'Client refilled drugs from another facility'\n" +
                "                     when 159367 then 'Client has enough drugs'\n" +
                "                     when 162619 then 'Client travelled'\n" +
                "                     when 126240 then 'Client could not get an off from work/school'\n" +
                "                     when 160583 then 'Client is sharing drugs with partner'\n" +
                "                     when 162192 then 'Client forgot clinic dates'\n" +
                "                     when 164349 then 'Client stopped medications'\n" +
                "                     when 1654 then 'Client sick at home/admitted'\n" +
                "                     when 5622 then 'Other' else ''  end) as reason_for_missed_appointment\n" +
                "from kenyaemr_etl.etl_ccc_defaulter_tracing;\n ";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
