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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.SpecificCauseOfDeathDataDefinition;
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
 * Evaluates specific cause of death Data Definition
 */
@Handler(supports= SpecificCauseOfDeathDataDefinition.class, order=50)
public class SpecificCauseOfDeathDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
        String qry = "select patient_id, coalesce((case specific_death_cause\n" +
                "                       when 165609 then 'COVID-19 Complications'\n" +
                "                       when 145439 then 'Non-communicable diseases such as Diabetes and hypertension'\n" +
                "                       when 156673 then 'HIV disease resulting in mycobacterial infection'\n" +
                "                       when 155010 then 'HIV disease resulting in Kaposis sarcoma'\n" +
                "                       when 156667 then 'HIV disease resulting in Burkitts lymphoma'\n" +
                "                       when 115195 then 'HIV disease resulting in other types of non-Hodgkin lymphoma'\n" +
                "                       when 157593 then 'HIV disease resulting in other malignant neoplasms of lymphoid and haematopoietic and related tissue'\n" +
                "                       when 156672 then 'HIV disease resulting in multiple malignant neoplasms'\n" +
                "                       when 159988 then 'HIV disease resulting in other malignant neoplasms'\n" +
                "                       when 5333 then 'HIV disease resulting in other bacterial infections'\n" +
                "                       when 116031 then 'HIV disease resulting in unspecified malignant neoplasms'\n" +
                "                       when 123122 then 'HIV disease resulting in other viral infections'\n" +
                "                       when 156669 then 'HIV disease resulting in cytomegaloviral disease'\n" +
                "                       when 156668 then 'HIV disease resulting in candidiasis'\n" +
                "                       when 5350 then 'HIV disease resulting in other mycoses'\n" +
                "                       when 882 then 'HIV disease resulting in Pneumocystis jirovecii pneumonia - HIV disease resulting in Pneumocystis carinii pneumonia'\n" +
                "                       when 156671 then 'HIV disease resulting in multiple infections'\n" +
                "                       when 160159 then 'HIV disease resulting in other infectious and parasitic diseases'\n" +
                "                       when 171 then 'HIV disease resulting in unspecified infectious or parasitic disease - HIV disease resulting in infection NOS'\n" +
                "                       when 156670 then 'HIV disease resulting in other specified diseases including encephalopathy or lymphoid interstitial pneumonitis or wasting syndrome and others'\n" +
                "                       when 160160 then 'HIV disease resulting in other conditions including acute HIV infection syndrome or persistent generalized lymphadenopathy or hematological and immunological abnormalities and others'\n" +
                "                       when 161548 then 'HIV disease resulting in Unspecified HIV disease'\n" +
                "                       else null end),natural_causes,non_natural_cause)\n" +
                "from kenyaemr_etl.etl_patient_program_discontinuation;";
        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
