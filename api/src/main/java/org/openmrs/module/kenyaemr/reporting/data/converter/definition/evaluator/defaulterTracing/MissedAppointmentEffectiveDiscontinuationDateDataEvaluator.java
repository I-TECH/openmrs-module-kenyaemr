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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.MissedAppointmentEffectiveDiscontinuationDateDataDefinition;
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
 * Returns the date when a patient was discontinued
 */
@Handler(supports= MissedAppointmentEffectiveDiscontinuationDateDataDefinition.class, order=50)
public class MissedAppointmentEffectiveDiscontinuationDateDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select encounter_id, if(discontinued is not null, date(discontinuation_date), null) effective_disc_date\n" +
                "from (select fup.encounter_id,\n" +
                "             d.patient_id   discontinued,\n" +
                "             coalesce(d.effective_discontinuation_date, d.visit_date) discontinuation_date\n" +
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
