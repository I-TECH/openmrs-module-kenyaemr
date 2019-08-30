/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.HTSClientsTestedPositiveNotLinkedCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

/**
 * Evaluator for patients for HTS Register - linkage and referral: those who tested positive but not linked
 */
@Handler(supports = {HTSClientsTestedPositiveNotLinkedCohortDefinition.class})
public class HTSClientsTestedPositiveNotLinkedCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	private final Log log = LogFactory.getLog(this.getClass());

	@Autowired
	EvaluationService evaluationService;
	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
		HTSClientsTestedPositiveNotLinkedCohortDefinition definition = (HTSClientsTestedPositiveNotLinkedCohortDefinition) cohortDefinition;

		if (definition == null)
			return null;

		Cohort newCohort = new Cohort();
		String qry=" select t.patient_id\n" +
				"from kenyaemr_etl.etl_hts_test t\n" +
				"  left join\n" +
				"((SELECT l.patient_id\n" +
				" from kenyaemr_etl.etl_hts_referral_and_linkage l\n" +
				"   inner join kenyaemr_etl.etl_patient_demographics pt on pt.patient_id=l.patient_id and pt.voided=0\n" +
				"   inner join kenyaemr_etl.etl_hts_test t on t.patient_id=l.patient_id and t.test_type in(1,2) and t.final_test_result='Positive' and t.visit_date <=l.visit_date and t.voided=0\n" +
				" where (l.ccc_number is not null or facility_linked_to is not null)\n" +
				")\n" +
				"union\n" +
				"( SELECT t.patient_id\n" +
				"  FROM kenyaemr_etl.etl_hts_test t\n" +
				"    INNER JOIN kenyaemr_etl.etl_patient_demographics pt ON pt.patient_id=t.patient_id AND pt.voided=0\n" +
				"    INNER JOIN kenyaemr_etl.etl_hiv_enrollment e ON e.patient_id=t.patient_id AND e.voided=0\n" +
				"  WHERE t.test_type IN (1, 2) AND t.final_test_result='Positive' AND t.voided=0\n" +
				")) l on l.patient_id = t.patient_id\n" +
				"where t.final_test_result = 'Positive' and t.voided = 0 and t.test_type=2 and l.patient_id is null\n" +
				";";

		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.append(qry);
		List<Integer> ptIds = evaluationService.evaluateToList(builder, Integer.class, context);
		newCohort.setMemberIds(new HashSet<Integer>(ptIds));
		return new EvaluatedCohort(newCohort, definition, context);
	}

}
