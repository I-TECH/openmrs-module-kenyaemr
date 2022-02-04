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

        String qry="SELECT patient_id, TIMESTAMPDIFF(MONTH, date(max(visit_date)), date(mid(max(concat(visit_date,next_appointment_date )),11))) as months_of_prescription\n" +
                "FROM kenyaemr_etl.etl_patient_hiv_followup\n" +
                "WHERE (select date(mid(max(concat(visit_date,next_appointment_date )),11))) is not null\n" +
                "GROUP BY patient_id;\n";

                SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
                queryBuilder.append(qry);
                Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
                c.setData(data);
        return c;
    }
}
