/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator.pmtct;

import org.openmrs.module.kenyaemr.reporting.cohort.definition.pmtct.PatientsOnMCHCohortDefinition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Evaluator for PatientsOnMCHCohortDefinition
 * Includes patients who are on MCH.
 */
@Handler(supports = {PatientsOnMCHCohortDefinition.class})
public class PatientsOnMCHCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

    private final Log log = LogFactory.getLog(this.getClass());

	@Autowired
	EvaluationService evaluationService;

    @Override
    public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		PatientsOnMCHCohortDefinition definition = (PatientsOnMCHCohortDefinition) cohortDefinition;

        if (definition == null)
            return null;

		Cohort newCohort = new Cohort();

		String qry="SELECT t.patient_id FROM (" +
				"SELECT e.patient_id, d.patient_id AS disc_patient," +
				" d.disc_date, max( e.visit_date ) AS latest_enrollment_date" +
				" FROM kenyaemr_etl.etl_mch_enrollment e JOIN" +
				" kenyaemr_etl.etl_patient_demographics p ON" +
				" p.patient_id = e.patient_id AND p.voided = 0 LEFT JOIN (SELECT" +
				" patient_id, max( visit_date ) AS disc_date FROM kenyaemr_etl.etl_patient_program_discontinuation" +
				" WHERE program_name = 'MCH Mother' AND date( visit_date ) <= date(:endDate )" +
				" GROUP BY patient_id ) d ON e.patient_id = d.patient_id " +
				" WHERE e.visit_date BETWEEN date(:startDate) and date(:endDate )" +
				" GROUP BY e.patient_id HAVING ( disc_patient IS NULL" +
				" OR date( latest_enrollment_date ) > date( disc_date ))) t;";

		SqlQueryBuilder builder = new SqlQueryBuilder();
		builder.append(qry);
		Date startDate = (Date)context.getParameterValue("startDate");
		Date endDate = (Date)context.getParameterValue("endDate");
		builder.addParameter("startDate", startDate);
		builder.addParameter("endDate", endDate);

		List<Integer> ptIds = evaluationService.evaluateToList(builder, Integer.class, context);
		newCohort.setMemberIds(new HashSet<Integer>(ptIds));
		return new EvaluatedCohort(newCohort, definition, context);
    }
}
