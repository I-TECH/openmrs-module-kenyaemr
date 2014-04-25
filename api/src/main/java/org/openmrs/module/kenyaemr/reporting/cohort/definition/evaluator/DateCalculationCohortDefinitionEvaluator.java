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

package org.openmrs.module.kenyaemr.reporting.cohort.definition.evaluator;

import java.util.Set;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.report.cohort.definition.evaluator.CalculationCohortDefinitionEvaluator;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.DateCalculationCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Evaluator for calculation based cohorts
 */
@Handler(supports = DateCalculationCohortDefinition.class, order = 10)
public class DateCalculationCohortDefinitionEvaluator extends CalculationCohortDefinitionEvaluator {
	
	/**
	 * @see org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator#evaluate(org.openmrs.module.reporting.cohort.definition.CohortDefinition,
	 *      org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {
		CalculationResultMap map = doCalculation(cohortDefinition, context);

		DateCalculationCohortDefinition cd = (DateCalculationCohortDefinition) cohortDefinition;
		Set<Integer> passing = EmrCalculationUtils.datesWithinRange(map, cd.getOnOrAfter(), cd.getOnOrBefore());

		return new EvaluatedCohort(new Cohort(passing), cohortDefinition, context);
	}
}