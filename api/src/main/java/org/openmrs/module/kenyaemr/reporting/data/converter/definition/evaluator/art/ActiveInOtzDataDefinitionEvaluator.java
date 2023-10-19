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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ActiveInOtzDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ActiveInOvcDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

/**
 * Evaluates Active in Otz Data Definition
 */
@Handler(supports= ActiveInOtzDataDefinition.class, order=50)
public class ActiveInOtzDataDefinitionEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select t.patient_id,\n" +
                "       if(disc_patient is null or date(enrollment_date) >= date(date_discontinued), 'Yes', 'No') as in_otz\n" +
                "from (select e.patient_id,\n" +
                "             d.patient_id      as disc_patient,\n" +
                "             max(d.visit_date) as date_discontinued,\n" +
                "             max(e.visit_date) as enrollment_date\n" +
                "      from kenyaemr_etl.etl_otz_enrollment e\n" +
                "               join kenyaemr_etl.etl_patient_demographics p\n" +
                "                    on p.patient_id = e.patient_id and p.voided = 0 and p.dead = 0\n" +
                "               left outer JOIN\n" +
                "           (select patient_id, visit_date\n" +
                "            from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "            where program_name = 'OTZ'\n" +
                "           ) d on d.patient_id = e.patient_id\n" +
                "      group by patient_id) t;";

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
