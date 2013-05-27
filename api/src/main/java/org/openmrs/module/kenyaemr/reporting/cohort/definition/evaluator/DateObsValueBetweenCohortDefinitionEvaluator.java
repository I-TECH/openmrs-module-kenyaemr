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

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientSetService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.DateObsValueBetweenCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.cohort.query.service.CohortQueryService;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

/**
 * Evaluates a DateObsValueBetweenCohortDefinition and produces a Cohort
 */
@Handler(supports={DateObsValueBetweenCohortDefinition.class})
public class DateObsValueBetweenCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	/**
	 * @see CohortDefinitionEvaluator#evaluate(org.openmrs.module.reporting.cohort.definition.CohortDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 * @should test any with many properties specified
	 * @should find nobody if no patients match
	 */
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		DateObsValueBetweenCohortDefinition cd = (DateObsValueBetweenCohortDefinition) cohortDefinition;

		Cohort c = Context.getService(CohortQueryService.class).getPatientsHavingRangedObs(
				PatientSetService.TimeModifier.ANY, cd.getQuestion(), cd.getGroupingConcept(),
				null, null,
				cd.getLocationList(), cd.getEncounterTypeList(),
				RangeComparator.GREATER_EQUAL, cd.getOnOrAfter(),
				RangeComparator.LESS_EQUAL, cd.getOnOrBefore());

		return new EvaluatedCohort(c, cohortDefinition, context);
	}
}