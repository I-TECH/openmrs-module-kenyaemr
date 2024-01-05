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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLDisclosureStatusDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLStabilityDataDefinition;
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
 * Evaluates Disclosure Status Data Definition
 */
@Handler(supports= ETLDisclosureStatusDataDefinition.class, order=50)
public class ETLDisclosureStatusDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry="select fup.patient_id,\n" +
                "        (case fup.pwp_pead_disclosure when 1066 then 'No disclosure' when 162979 then 'Partial disclosure' when 166982 then 'Full disclosure' else '' end) as Disclosure_status\n" +
                "                from\n" +
                "                 (select f.patient_id,\n" +
                "      mid(max(concat(f.visit_date,f.pwp_pead_disclosure)),11) as pwp_pead_disclosure,\n" +
                "      f.person_present person_present,\n" +
                "      f.visit_date visit_date\n" +
                "    from kenyaemr_etl.etl_patient_hiv_followup f\n" +
                "    where person_present = 978 and f.voided = 0 and date(visit_date) <= date(:endDate)\n" +
                "    group by f.patient_id ) fup;";

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
