/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.AppointmentTypeNotHonouredDataDefinition;
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
 * Evaluates a VisitIdDataDefinition to produce a VisitData
 */
@Handler(supports= AppointmentTypeNotHonouredDataDefinition.class, order=50)
public class AppointmentNotHonouredTypeDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select encounter_id, mApp.type_of_missed_appointment\n" +
                "from (select fup.encounter_id,\n" +
                "             honoredVisit.patient_id                                                                             honored_appt,\n" +
                "             honoured_refill.patient_id                                                                       as honouredRefill,\n" +
                "             if(date(fup.next_appointment_date) between date(:startDate) and date(:endDate), 'Clinical',\n" +
                "                if(date(fup.refill_date) between date(:startDate) and date(:endDate), 'Refill',\n" +
                "                   ''))                                                                                       as type_of_missed_appointment\n" +
                "      from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "               left join kenyaemr_etl.etl_patient_hiv_followup honoredVisit\n" +
                "                         on honoredVisit.patient_id = fup.patient_id and\n" +
                "                            honoredVisit.next_appointment_date = fup.visit_date and honoredVisit.visit_date > fup.visit_date\n" +
                "               left join kenyaemr_etl.etl_art_fast_track honoured_refill\n" +
                "                         on honoured_refill.patient_id = fup.patient_id and honoured_refill.visit_date = fup.refill_date and honoured_refill.visit_date > fup.visit_date\n" +
                "               join kenyaemr_etl.etl_patient_demographics p on p.patient_id = fup.patient_id\n" +
                "               join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id = e.patient_id\n" +
                "      where date(fup.next_appointment_date) between date(:startDate) and date(:endDate)\n" +
                "         or date(fup.refill_date) between date(:startDate) and date(:endDate)\n" +
                "      group by fup.encounter_id, fup.patient_id\n" +
                "      having honored_appt is null\n" +
                "         and honouredRefill is null) mApp;";

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
