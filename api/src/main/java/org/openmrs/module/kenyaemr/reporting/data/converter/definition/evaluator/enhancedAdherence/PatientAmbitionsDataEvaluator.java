/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.enhancedAdherence;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.enhancedAdherence.PatientAmbitionsDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.enhancedAdherence.PatientDrugsDailyUptakeFeelingDataDefinition;
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
@Handler(supports= PatientAmbitionsDataDefinition.class, order=50)
public class PatientAmbitionsDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select patient_id,patient_ambitions from kenyaemr_etl.etl_enhanced_adherence group by patient_id;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}