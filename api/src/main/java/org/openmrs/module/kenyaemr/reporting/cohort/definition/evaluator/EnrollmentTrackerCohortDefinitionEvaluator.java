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
import org.openmrs.module.kenyaemr.reporting.cohort.definition.EnrollmentTrackerCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.RDQAActiveCohortDefinition;
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
 * Evaluator for enrollment tracker cohort - those tested and linked
 */
@Handler(supports = {EnrollmentTrackerCohortDefinition.class})
public class EnrollmentTrackerCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());

	@Autowired
	EvaluationService evaluationService;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		EnrollmentTrackerCohortDefinition definition = (EnrollmentTrackerCohortDefinition) cohortDefinition;

        if (definition == null)
            return null;

		Cohort newCohort = new Cohort();

		String qry = "SELECT t.patient_id\n" +
				"FROM kenyaemr_etl.etl_hts_test t\n" +
				"left join kenyaemr_etl.etl_hts_referral_and_linkage l on l.patient_id=t.patient_id and t.visit_date <=l.visit_date\n" +
				"left join kenyaemr_etl.etl_hiv_enrollment e on e.patient_id = t.patient_id and e.voided=0\n" +
				"where t.final_test_result='Positive' and t.voided=0 and (l.patient_id is not null or e.patient_id is not null)\n" +
				";";

		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.append(qry);
		List<Integer> ptIds = evaluationService.evaluateToList(builder, Integer.class, context);
		newCohort.setMemberIds(new HashSet<Integer>(ptIds));
		return new EvaluatedCohort(newCohort, definition, context);
    }
}
