/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.PCREIDAt8MonthsDataDefinition;
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
 * Evaluates a patient's PCR/EID at 8 months
 */
@Handler(supports = PCREIDAt8MonthsDataDefinition.class, order = 50)
public class PCREIDAt8MonthsDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select d.patient_id,ifnull(coalesce(case x.lab_test when 1030 then 'Yes' else NULL end,case hv.dna_pcr_sample_date when NOT NULL then 'Yes' else NULL end ),'Missing') as initial_EID from kenyaemr_etl.etl_patient_demographics d\n" +
                "               left join (select x.patient_id,x.lab_test as lab_test,x.date_test_requested as date_test_requested from kenyaemr_etl.etl_laboratory_extract x\n" +
                "                         ) x on d.patient_id = x.patient_id\n" +
                "               left join (select hv.patient_id,hv.dna_pcr_sample_date as dna_pcr_sample_date from kenyaemr_etl.etl_hei_follow_up_visit hv)hv on d.patient_id = hv.patient_id\n" +
                "where (timestampdiff(WEEK,d.DOB,x.date_test_requested) <=8) or\n" +
                "       (timestampdiff(WEEK,d.DOB,hv.dna_pcr_sample_date) <=8) group by d.patient_id;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}