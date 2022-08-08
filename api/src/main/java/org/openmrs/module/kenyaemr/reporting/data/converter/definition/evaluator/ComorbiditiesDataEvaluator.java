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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.ComorbiditiesDataDefinition;
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
 * Evaluates co-morbidities Data Definition
 */
@Handler(supports= ComorbiditiesDataDefinition.class, order=50)
public class ComorbiditiesDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
        String qry = "select a.patient_id,group_concat(case chronic_illness when 149019 then 'Alzheimers Disease and other Dementias'\n" +
                "                                          when 148432 then 'Arthritis'\n" +
                "                                          when 153754 then 'Asthma'\n" +
                "                                          when 159351 then 'Cancer'\n" +
                "                                          when 119270 then 'Cardiovascular diseases'\n" +
                "                                          when 120637 then 'Chronic Hepatitis'\n" +
                "                                          when 145438 then 'Chronic Kidney Disease'\n" +
                "                                          when 1295 then 'Chronic Obstructive Pulmonary Disease(COPD)'\n" +
                "                                          when 120576 then 'Chronic Renal Failure'\n" +
                "                                          when 119692 then 'Cystic Fibrosis'\n" +
                "                                          when 120291 then 'Deafness and Hearing impairment'\n" +
                "                                          when 119481 then 'Diabetes'\n" +
                "                                          when 118631 then 'Endometriosis'\n" +
                "                                          when 117855 then 'Epilepsy'\n" +
                "                                          when 117789 then 'Glaucoma'\n" +
                "                                          when 139071 then 'Heart Disease'\n" +
                "                                          when 115728 then 'Hyperlipidaemia'\n" +
                "                                          when 117399 then 'Hypertension'\n" +
                "                                          when 117321 then 'Hypothyroidism'\n" +
                "                                          when 151342 then 'Mental illness'\n" +
                "                                          when 133687 then 'Multiple Sclerosis'\n" +
                "                                          when 115115 then 'Obesity'\n" +
                "                                          when 114662 then 'Osteoporosis'\n" +
                "                                          when 117703 then 'Sickle Cell Anaemia'\n" +
                "                                          when 118976 then 'Thyroid disease'\n" +
                "    end) from kenyaemr_etl.etl_allergy_chronic_illness a group by a.patient_id;";
        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
       // Date startDate = (Date)context.getParameterValue("startDate");
        //queryBuilder.addParameter("startDate", startDate);
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
