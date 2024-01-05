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
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.PopulationTypeDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.PopulationTypeArtDataDefinition;
import org.openmrs.module.reporting.data.encounter.EvaluatedEncounterData;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.evaluator.EncounterDataEvaluator;
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
 * Evaluates a VisitIdDataDefinition to produce a VisitData
 */
@Handler(supports=PopulationTypeArtDataDefinition.class, order=50)
public class PopulationTypeArtDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select\n" +
                "fup.patient_id,\n" +
                "  CONCAT_WS('\\r\\n',fup.population_type,fup.key_population_type) as Population_Type\n" +
                "from  (SELECT patient_id,\n" +
                "         mid(min(concat(visit_date,(case population_type\n" +
                "                                    when 164928 then \"General Population\"\n" +
                "                                    when 164929 then \"Key Population\" else \"\" end), \"\" )),11) as population_type,\n" +
                "         mid(min(concat(visit_date,(case key_population_type\n" +
                "                                    when 105 then \"People who inject drugs\"\n" +
                "                                    when 162277 then \"People in prison and other closed settings\"\n" +
                "                                    when 165100 then \"Transgender\"\n" +
                "                                    when 160578 then \"Men who have sex with men\"\n" +
                "                                    when 160579 then \"Female sex Worker\" else \"\" end), \"\" )),11) as key_population_type\n" +
                "        FROM kenyaemr_etl.etl_patient_hiv_followup GROUP BY patient_id) fup\n" +
                "GROUP BY fup.patient_id;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}
