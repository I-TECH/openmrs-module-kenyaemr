/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.art;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLMonthsOfPrescriptionDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Months Of Prescription Data Evaluator
 */
@Handler(supports= ETLMonthsOfPrescriptionDataDefinition.class, order=50)
public class ETLMonthsOfPrescriptionDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

//        String qry="select patient_id,\n" +
//                "  (case cic.stability when 1 then \"Stable\" when 2 then \"Unstable\" else \"\" end) as Stability\n" +
//                "                          from(select c.patient_id,f.stability stability,f.person_present patient_present,c.latest_vis_date latest_visit_date,f.visit_date fup_visit_date,c.latest_tca ltca  from kenyaemr_etl.etl_current_in_care c\n" +
//                "                                        inner join kenyaemr_etl.etl_patient_hiv_followup f on f.patient_id = c.patient_id and c.latest_vis_date =f.visit_date\n" +
//                "                                      where c.started_on_drugs is not null  and f.voided = 0 group by c.patient_id) cic ;";

                        String qry="select patient_id,\n" +
                                "CASE \n" +
                                "WHEN refill_date is null then \n" +
                                "round(DATEDIFF(date(mid(max(concat(visit_date, next_appointment_date )),11)), date(max(visit_date)))/30)\n" +
                                "ELSE\n" +
                                "round(DATEDIFF( date(mid(max(concat(visit_date,refill_date )),11)), date(max(visit_date)))/30)\n" +
                                "END AS months_of_prescription\n" +
                                "from kenyaemr_etl.etl_patient_hiv_followup\n" +
                                "GROUP BY patient_id;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
