/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.ipt;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.ipt.MonthlyDrugCollectionDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.ipt.PostIPTTBStatusDateDataDefinition;
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
 * Evaluates Month1DrugCollectionDateDataDefinition
 */
@Handler(supports = PostIPTTBStatusDateDataDefinition.class, order = 50)
public class PostIPTTBStatusDateDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
        PostIPTTBStatusDateDataDefinition mdef = (PostIPTTBStatusDateDataDefinition) definition;
        Integer maxDays = mdef.getMinDays();
        Integer minDays = mdef.getMinDays();


    String qry = "Select init.patient_id,COALESCE(concat_ws('\\r\\n',fup.tb_status,max(fup.visit_date)),concat_ws('\\r\\n',tbs.resulting_tb_status,max(tbs.visit_date))) as tb_status_date_m6 from kenyaemr_etl.etl_ipt_initiation init\n" +
            "                                                                                  left outer join kenyaemr_etl.etl_patient_program_discontinuation d on init.patient_id = d.patient_id\n" +
            "                                                                                  left outer join kenyaemr_etl.etl_tb_screening tbs on init.patient_id = tbs.patient_id\n" +
            "                                                                                  left outer join kenyaemr_etl.etl_patient_hiv_followup fup on init.patient_id = fup.patient_id\n" +
            "where d.program_name = \"IPT\" and timestampdiff(DAY ,d.visit_date,COALESCE(fup.visit_date,tbs.visit_date))  between :minDays and :maxDays\n" +
            "group by init.patient_id;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        Date startDate = (Date)context.getParameterValue("startDate");
        Date endDate = (Date)context.getParameterValue("endDate");
        queryBuilder.addParameter("endDate", endDate);
        queryBuilder.addParameter("startDate", startDate);
        queryBuilder.addParameter("minDays", minDays);
        queryBuilder.addParameter("maxDays", maxDays);
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;

    }
}