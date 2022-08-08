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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.ipt.CTXDapsoneStartDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.ipt.HIVTestDateDataDefinition;
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
 * Evaluates HIVTestDateDataDefinition
 */
@Handler(supports= CTXDapsoneStartDateDataDefinition.class, order=50)
public class CTXDapsoneStartDateDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select init.patient_id,coalesce(date(fup.visit_date),date(o.date_stopped)) as date_started_ctx_dapsone from kenyaemr_etl.etl_ipt_initiation init\n" +
                "    left outer join kenyaemr_etl.etl_patient_hiv_followup fup on init.patient_id = fup.patient_id\n" +
                "                    and (fup.ctx_dispensed =105281 or fup.ctx_dispensed = 1065 or fup.dapsone_dispensed = 74250 or fup.dapsone_dispensed = 1065)\n" +
                "                    left outer join orders o on init.patient_id = o.patient_id\n" +
                "                    inner join drug_order do on o.order_id = do.order_id and o.concept_id in (105281,74250)\n" +
                "                where init.voided = 0  and (date(fup.visit_date) <= date(init.visit_date) or date(o.date_stopped) <= date(init.visit_date)) group by init.patient_id;";

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