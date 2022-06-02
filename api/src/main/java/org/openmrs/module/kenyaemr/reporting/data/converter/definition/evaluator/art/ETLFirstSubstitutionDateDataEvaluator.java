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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLFirstSubstitutionDateDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLRefillDateDataDefinition;
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
 * Evaluates First substitution Data Definition
 */
@Handler(supports= ETLFirstSubstitutionDateDataDefinition.class, order=50)
public class ETLFirstSubstitutionDateDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select\n" +
                "  fdr.patient_id,\n" +
                "  fdr.firstSubstitution_date as first_substitution_date\n" +
                "from  (SELECT\n" +
                "         mid(max(concat(visit_date,encounter_id)),11) as patient_id,\n" +
                "         mid(max(concat(visit_date,regimen)),11) as firstSubstitution,\n" +
                "         mid(max(concat(visit_date,date_started)),11) as firstSubstitution_date,\n" +
                "         mid(min(concat(visit_date,date_started)),11) as dateStarted,\n" +
                "         mid(max(concat(visit_date,regimen_line)),11) as regimenLine,\n" +
                "         COUNT(patient_id) as p_id FROM kenyaemr_etl.etl_drug_event  GROUP BY patient_id  HAVING p_id = 2) fdr\n" +
                "GROUP BY fdr.patient_id;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
