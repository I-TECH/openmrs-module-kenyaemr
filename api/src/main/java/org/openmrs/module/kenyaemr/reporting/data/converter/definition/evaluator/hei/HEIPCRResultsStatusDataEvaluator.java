/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.hei;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.hei.HEIPCRResultsStatusDataDefinition;
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
 * Evaluates a PersonDataDefinition
 */
@Handler(supports= HEIPCRResultsStatusDataDefinition.class, order=50)
public class HEIPCRResultsStatusDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select f.patient_id,\n" +
                "       (case dna_pcr_result when 1138 then \"INDETERMINATE\" when 664 then \"NEGATIVE\" when 703 then \"POSITIVE\" when 1304 then \"POOR SAMPLE QUALITY\" else \"\"  end) as dna_pcr_result\n" +
                "from  (\n" +
                "        select t.patient_id, left(max(concat(t.visit_date, t.dna_pcr_result)),10) visit_date, mid(max(concat(t.visit_date, t.dna_pcr_result)),11) dna_pcr_result\n" +
                "        from\n" +
                "          (\n" +
                "            (select patient_id, visit_date, test_result dna_pcr_result\n" +
                "             from kenyaemr_etl.etl_laboratory_extract\n" +
                "             where lab_test = 1030\n" +
                "            )\n" +
                "            union\n" +
                "            (\n" +
                "              select patient_id, visit_date, dna_pcr_result\n" +
                "              from kenyaemr_etl.etl_hei_follow_up_visit\n" +
                "              where dna_pcr_result is not null\n" +
                "            )\n" +
                "          ) t\n" +
                "        group by t.patient_id\n" +
                "        ) f\n" +
                "        INNER JOIN kenyaemr_etl.etl_patient_demographics d ON f.patient_id = d.patient_id\n" +
                "WHERE round(DATEDIFF(f.visit_date,d.DOB)/7) BETWEEN 0 AND 6\n" +
                "GROUP BY f.patient_id;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}