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

		String qry="select t.patient_id \n" +
				"from \n" +
				"(select e.patient_id, d.patient_id as disc_patient \n" +
				",max(d.visit_date) as date_discontinued, \n" +
				"max(e.visit_date) as enrollment_date \n" +
				"from kenyaemr_etl.etl_mch_enrollment e \n" +
				"join kenyaemr_etl.etl_patient_demographics p \n" +
				"on p.patient_id=e.patient_id and p.voided=0 \n" +
				"and p.dead=0 left outer JOIN \n" +
				"(select patient_id,visit_date \n" +
				"from kenyaemr_etl.etl_patient_program_discontinuation \n" +
				"where program_name='MCH' group by patient_id ) d \n" +
				"on d.patient_id = e.patient_id group by patient_id \n" +
				"having (disc_patient is null or date(enrollment_date) >= date(date_discontinued) ))t;";

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
