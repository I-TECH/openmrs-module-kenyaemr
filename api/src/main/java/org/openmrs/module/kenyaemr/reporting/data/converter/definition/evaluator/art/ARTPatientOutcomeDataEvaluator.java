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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ARTFirstSubstitutionDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ARTPatientOutcomeDataDefinition;
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
 * ART Patient Outcome Data Evaluator Column
 */
@Handler(supports=ARTPatientOutcomeDataDefinition.class, order=50)
public class ARTPatientOutcomeDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
        String qry = "select\n" +
                "  fdr.patient_id,\n" +
                "  if(disc.outcome is null,\"Alive on ART\",disc.outcome) as outcome\n" +
                "from  (SELECT patient_id\n" +
                "       FROM kenyaemr_etl.etl_drug_event WHERE program=\"HIV\" ) fdr\n" +
                "  left join (SELECT patient_id,\n" +
                "               mid(max(concat(visit_date,(case discontinuation_reason\n" +
                "                                          when 159492 then \"Transferred Out\"\n" +
                "                                          when 160034 then \"Died\"\n" +
                "                                          when 5240 then \"Lost to Follow\"\n" +
                "                                          when 819 then \"Cannot afford Treatment\"\n" +
                "                                          when 5622 then \"Other\"\n" +
                "                                          when 1067 then \"Unknown\"\n" +
                "                                          else \"\" end), \"\" )),20) as outcome,\n" +
                "               COUNT(patient_id) as p_id FROM kenyaemr_etl.etl_patient_program_discontinuation WHERE program_name='HIV' GROUP BY patient_id  HAVING p_id > 0) disc\n" +
                "    on disc.patient_id = fdr.patient_id\n" +
                "GROUP BY fdr.patient_id;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
