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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.defaulterTracing.HonouredAppointmentDateDataDefinition;
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
 * Evaluates return to care date
 */
@Handler(supports=HonouredAppointmentDateDataDefinition.class, order=50)
public class HonouredAppointmentDateDataEvaluator implements EncounterDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedEncounterData evaluate(EncounterDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedEncounterData c = new EvaluatedEncounterData(definition, context);

        String qry = "select b.encounter_id, b.rtc_date\n" +
                "              from (select a.encounter_id, a.lost_clinical_visit_date, a.lost_tca, a.lost_refill_tca, a.latest_tracing_date, a.rtc_date\n" +
                "                    from (select t.encounter_id,\n" +
                "                                 f.patient_id,\n" +
                "                                 f.visit_date,\n" +
                "                                 min(f.visit_date)                                           as rtc_date,\n" +
                "                                 t.latest_tracing_date                                       as latest_tracing_date,\n" +
                "                                 t.lost_visit                                                as lost_clinical_visit_date,\n" +
                "                                 t.lost_tca                                                  as lost_tca,\n" +
                "                                 t.lost_refill_tca                                           as lost_refill_tca\n" +
                "                          from kenyaemr_etl.etl_patient_hiv_followup f\n" +
                "                                   inner join\n" +
                "                               (select t.patient_id,\n" +
                "                                       t.encounter_id,\n" +
                "                                       max(t.visit_date)                                                       as latest_tracing_date,\n" +
                "                                       max(f.visit_date)                                                       as lost_visit,\n" +
                "                                       mid(max(concat(date(f.visit_date), date(f.next_appointment_date))), 11) as lost_tca,\n" +
                "                                       mid(max(concat(date(f.visit_date), date(f.refill_date))), 11)              lost_refill_tca\n" +
                "                                from kenyaemr_etl.etl_ccc_defaulter_tracing t\n" +
                "                                         inner join\n" +
                "                                     kenyaemr_etl.etl_patient_hiv_followup f\n" +
                "                                     on t.patient_id = f.patient_id\n" +
                "                                where t.visit_date between date(:startDate) and date(:endDate) and t.visit_date > f.visit_date\n" +
                "                                group by t.patient_id) t on f.patient_id = t.patient_id\n" +
                "                          where f.visit_date >= latest_tracing_date\n" +
                "                          group by f.encounter_id) a) b;";

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
