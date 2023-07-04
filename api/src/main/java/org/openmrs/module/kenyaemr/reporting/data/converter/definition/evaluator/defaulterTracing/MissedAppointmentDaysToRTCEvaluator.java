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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.MissedAppointmentDaysToRTCDataDefinition;
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
 * Returns duration taken by a patient to return to care
 */
@Handler(supports= MissedAppointmentDaysToRTCDataDefinition.class, order=50)
public class MissedAppointmentDaysToRTCEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select encounter_id, timestampdiff(DAY, missed_appointment_date, rtc_date) daysToRtc\n" +
                "from (select fup.encounter_id, fup.next_appointment_date missed_appointment_date, min(firstVisit.visit_date) as rtc_date\n" +
                "      from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "               left join kenyaemr_etl.etl_patient_hiv_followup firstVisit\n" +
                "                         on firstVisit.patient_id = fup.patient_id and\n" +
                "                            fup.next_appointment_date < firstVisit.visit_date\n" +
                "               join kenyaemr_etl.etl_patient_demographics p on p.patient_id = fup.patient_id\n" +
                "               join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id = e.patient_id\n" +
                "      where date(fup.next_appointment_date) between date(:startDate) and date(:endDate)\n" +
                "      group by fup.encounter_id, fup.patient_id\n" +
                "      having rtc_date is not null) t;";

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
