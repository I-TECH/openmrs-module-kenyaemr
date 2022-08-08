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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLLastVLJustificationDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLLastVLResultDataDefinition;
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
 * Evaluates Last VL result Data Definition
 */
@Handler(supports= ETLLastVLJustificationDataDefinition.class, order=50)
public class ETLLastVLJustificationDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select patient_id,case mid(max(concat(visit_date, order_reason)),11) when 843 then 'Confirmation of treatment failure (repeat VL)' when 1259 then 'Single Drug Substitution' when 1434 then 'Pregnancy'\n" +
                "     when 159882 then 'Breastfeeding' when 160566 then 'Immunologic failure' when 160569 then 'Virologic failure'\n" +
                "     when 161236 then 'Routine' when 162080 then 'Baseline VL (for infants diagnosed through EID)' when 162081 then 'Repeat' when 163523 then 'Clinical failure'\n" +
                "     when 160032 then 'Confirmation of persistent low level Viremia (PLLV)' when 1040 then 'Initial PCR (6week or first contact)' when 1326 then '2nd PCR (6 months)' when 164860 then '3rd PCR (12months)'\n" +
                "     when 162082 then 'Confirmatory PCR and Baseline VL' when 164460 then 'Ab test 6 weeks after cessation of breastfeeding'\n" +
                "     when 164860 then 'Ab test at 18 months (1.5 years)'\n" +
                "     else '' end as justification from kenyaemr_etl.etl_laboratory_extract where lab_test in (856,1305)\n" +
                "GROUP BY patient_id;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
