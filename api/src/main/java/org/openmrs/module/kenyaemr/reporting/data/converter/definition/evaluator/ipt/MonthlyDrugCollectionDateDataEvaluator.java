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
@Handler(supports = MonthlyDrugCollectionDateDataDefinition.class, order = 50)
public class MonthlyDrugCollectionDateDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
        MonthlyDrugCollectionDateDataDefinition mdef = (MonthlyDrugCollectionDateDataDefinition) definition;
        Integer maxMonth = mdef.getMaxMonth();
        Integer minMonth = mdef.getMinMonth();


    String qry = "select init.patient_id, coalesce(date(f.date_collected_ipt),date(x.date_stopped)) as month1_refill from kenyaemr_etl.etl_ipt_initiation init\n" +
            "    left outer join kenyaemr_etl.etl_ipt_follow_up f on init.patient_id = f.patient_id\n" +
            "                                                                       left outer join (select o.patient_id,o.date_stopped from openmrs.orders o\n" +
            "                                                                       inner join openmrs.drug_order do on o.order_id = do.order_id where o.concept_id = 78280) x on init.patient_id = x.patient_id\n" +
            "where timestampdiff(Month,init.visit_date,coalesce(f.date_collected_ipt,x.date_stopped)) between :minMonth and :maxMonth group by init.patient_id;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        Date startDate = (Date)context.getParameterValue("startDate");
        Date endDate = (Date)context.getParameterValue("endDate");
        queryBuilder.addParameter("endDate", endDate);
        queryBuilder.addParameter("startDate", startDate);
        queryBuilder.addParameter("minMonth", minMonth);
        queryBuilder.addParameter("maxMonth", maxMonth);
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;

    }
}