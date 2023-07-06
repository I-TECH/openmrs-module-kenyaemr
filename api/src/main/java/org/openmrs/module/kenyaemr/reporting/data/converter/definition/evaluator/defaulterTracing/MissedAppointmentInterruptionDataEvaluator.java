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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.MissedAppointmentInterruptionDataDefinition;
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
 * Indicates whether a missed appointment require tracing or not.
 * Returns if a client was discontinued after the visit when the appointment was given, or an active status
 */
@Handler(supports= MissedAppointmentInterruptionDataDefinition.class, order=50)
public class MissedAppointmentInterruptionDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select encounter_id, if(discontinued is not null, discontinuation_reason, 'Active') status\n" +
                "from (select fup.encounter_id,\n" +
                "             d.patient_id   discontinued,\n" +
                "             case d.discontinuation_reason\n" +
                "                 when 159492 then 'TO'\n" +
                "                 when 160034 then 'Died'\n" +
                "                 when 5240 then 'LTFU'\n" +
                "                 when 819 then 'Cannot afford treatment'\n" +
                "                 when 5622 then 'Other'\n" +
                "                 when 164349 then 'Unknown'\n" +
                "             else '' end discontinuation_reason\n" +
                "      from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "               left join kenyaemr_etl.etl_patient_program_discontinuation d on d.patient_id = fup.patient_id and d.program_name='HIV' and\n" +
                "                                                                               coalesce(d.effective_discontinuation_date, d.visit_date) between fup.visit_date and fup.next_appointment_date\n" +
                "      where next_appointment_date between date(:startDate) and date(:endDate)\n" +
                "      group by fup.encounter_id) t;";

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
