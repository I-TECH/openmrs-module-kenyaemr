/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.pama;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pama.PamaBaselineVLDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.pama.PamaCareGiverBaselineVLDataDefinition;
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
 * Evaluates Care Giver Baseline VL Data Definition
 */
@Handler(supports = PamaCareGiverBaselineVLDataDefinition.class, order = 50)
public class PamaCareGiverBaselineVLDataDefinitionEvaluator implements PersonDataEvaluator {
	
	@Autowired
	private EvaluationService evaluationService;
	
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
		
		String qry = "select distinct r.person_a,\n" +
				"    mid(max(concat(le.visit_date, if(le.lab_test = 856, le.test_result, if(le.lab_test=1305 and le.test_result = 1302, 'LDL','')), '' )),11) as vl_result\n" +
				"   from kenyaemr_etl.etl_patient_demographics d\n" +
				"     inner join kenyaemr_etl.etl_laboratory_extract le on le.patient_id = d.patient_id and le.lab_test in (1305,856)\n" +
				"     inner join openmrs.relationship r on d.patient_id = r.person_b\n" +
				"     inner join openmrs.relationship_type t on r.relationship = t.relationship_type_id and t.uuid = '3667e52f-8653-40e1-b227-a7278d474020'\n" +
				"                                               and (le.visit_date <= date(r.start_date))\n" +
				" group by r.person_a;";
		
		SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
		queryBuilder.append(qry);
		Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
		c.setData(data);
		return c;
	}
}
