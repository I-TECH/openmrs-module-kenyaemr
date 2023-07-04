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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.MissedAppointmentTracingFinalOutcomeDataDefinition;
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
 * Returns a patients tracing final outcome
 */
@Handler(supports= MissedAppointmentTracingFinalOutcomeDataDefinition.class, order=50)
public class MissedAppointmentTracingFinalOutcomeDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select fup.encounter_id,\n" +
                "       case tr.true_status\n" +
                "           when 160432 then 'Dead'\n" +
                "           when 1693 then 'Receiving ART from another clinic'\n" +
                "           when 160037 then 'Still in care at CCC'\n" +
                "           when 5240 then 'Lost to follow up'\n" +
                "           when 164435 then 'Stopped treatment'\n" +
                "           when 142917 then 'Other'\n" +
                "       else '' end\n" +
                "               final_outcome\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "         join kenyaemr_etl.etl_ccc_defaulter_tracing tr on tr.patient_id = fup.patient_id and fup.next_appointment_date = tr.missed_appointment_date and tr.true_status is not null and tr.true_status > 0\n" +
                "where next_appointment_date between date(:startDate) and date(:endDate)\n" +
                "group by fup.encounter_id;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        Date startDate = (Date)context.getParameterValue("startDate");
        Date endDate = (Date)context.getParameterValue("endDate");
        queryBuilder.addParameter("endDate", endDate);
        queryBuilder.addParameter("startDate", startDate);
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
