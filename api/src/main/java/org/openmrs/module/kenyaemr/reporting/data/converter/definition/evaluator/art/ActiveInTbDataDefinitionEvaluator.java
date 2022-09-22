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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ActiveInOvcDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ActiveInTbDataDefinition;
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
 * Evaluates Active in Tb Data Definition
 */
@Handler(supports= ActiveInTbDataDefinition.class, order=50)
public class ActiveInTbDataDefinitionEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select d.patient_id,if(v.program_client is not null or c.hiv_client is not null,'Yes','No') from kenyaemr_etl.etl_patient_demographics d\n" +
                "  left join (select pp.patient_id as program_client from patient_program pp\n" +
                "    inner join program p on p.program_id = pp.program_id and p.name ='TB' and date(pp.date_enrolled) <= date(:endDate)\n" +
                "  where date(pp.date_completed) is null) v on d.patient_id=v.program_client\n" +
                "  left join (select v.patient_id as hiv_client,max(date(v.visit_date)),mid(max(concat(date(v.visit_date),v.on_anti_tb_drugs)),11) as on_tb_drugs\n" +
                "  from kenyaemr_etl.etl_patient_hiv_followup v\n" +
                "  where date(v.visit_date) between date(:startDate) and date(:endDate)\n" +
                "  group by v.patient_id having max(date(visit_date)) <= date(:endDate) and on_tb_drugs = 1065)  c on d.patient_id = c.hiv_client;";

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
