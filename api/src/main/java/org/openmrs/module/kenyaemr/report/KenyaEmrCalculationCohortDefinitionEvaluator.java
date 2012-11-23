/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.kenyaemr.report;

import java.util.Date;
import java.util.Set;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.calculation.CalculationUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 *
 */
@Handler(supports = KenyaEmrCalculationCohortDefinition.class)
public class KenyaEmrCalculationCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	/**
	 * @see org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator#evaluate(org.openmrs.module.reporting.cohort.definition.CohortDefinition,
	 *      org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
		KenyaEmrCalculationCohortDefinition cd = (KenyaEmrCalculationCohortDefinition) cohortDefinition;
		Date date = (Date) context.getParameterValue("date");
		if (date == null) {
			date = (Date) context.getParameterValue("endDate");
		}
		
		PatientCalculationService pcs = Context.getService(PatientCalculationService.class);
		PatientCalculationContext calcContext = pcs.createCalculationContext();
		if (date != null) {
			calcContext.setNow(date);
		}
		Cohort cohort = context.getBaseCohort();
		if (cohort == null) {
			cohort = Context.getPatientSetService().getAllPatients();
		}
		CalculationResultMap map = pcs.evaluate(cohort.getMemberIds(), cd.getCalculation(), calcContext);
		Set<Integer> passing;
		if (cd.getResultOnOrAfter() != null || cd.getResultOnOrBefore() != null) {
			passing = CalculationUtils.datesWithinRange(map, cd.getResultOnOrAfter(), cd.getResultOnOrBefore());
		} else {
			passing = CalculationUtils.patientsThatPass(map);
		}
		return new EvaluatedCohort(new Cohort(passing), cd, context);
	}
	
}
