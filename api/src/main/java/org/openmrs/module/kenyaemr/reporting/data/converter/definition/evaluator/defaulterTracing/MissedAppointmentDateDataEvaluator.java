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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.MissedAppointmentDateDataDefinition;
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
 * Returns the date when a patient missed an appointment
 */
@Handler(supports= MissedAppointmentDateDataDefinition.class, order=50)
public class MissedAppointmentDateDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select encounter_id,\n" +
                "       ifnull(missed_refill_date, least(ifnull(missed_refill_date, missed_appointment_date),\n" +
                "                                        missed_appointment_date)) as date_missed_appointment\n" +
                "from (select fup.encounter_id,\n" +
                "             fup.patient_id,\n" +
                "             fup.next_appointment_date       missed_appointment_date,\n" +
                "             fup.refill_date                 missed_refill_date,\n" +
                "             min(firstVisit.visit_date)   as rtcDate,\n" +
                "             min(refill_visit.visit_date) as rtc_refill\n" +
                "      from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "               left join kenyaemr_etl.etl_patient_hiv_followup firstVisit\n" +
                "                         on firstVisit.patient_id = fup.patient_id and\n" +
                "                            fup.next_appointment_date < firstVisit.visit_date\n" +
                "               left join kenyaemr_etl.etl_art_fast_track refill_visit\n" +
                "                         on refill_visit.patient_id = fup.patient_id and fup.refill_date < refill_visit.visit_date\n" +
                "               join kenyaemr_etl.etl_patient_demographics p on p.patient_id = fup.patient_id\n" +
                "               join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id = e.patient_id\n" +
                "      where date(fup.next_appointment_date) between date(:startDate) and date(:endDate)\n" +
                "         or date(fup.refill_date) between date(:startDate) and date(:endDate)\n" +
                "      group by fup.encounter_id, fup.patient_id) t;";

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
