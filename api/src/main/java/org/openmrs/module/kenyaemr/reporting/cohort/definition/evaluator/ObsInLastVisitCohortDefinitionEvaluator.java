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
import org.openmrs.Obs;
import org.openmrs.Visit;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.ObsInLastVisitCohortDefinition;
import org.openmrs.module.kenyaemr.reporting.data.patient.definition.VisitsForPatientDataDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.data.person.PersonData;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.util.OpenmrsUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Evaluator for {@link ObsInLastVisitCohortDefinition}
 */
@Handler(supports = ObsInLastVisitCohortDefinition.class)
public class ObsInLastVisitCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	/**
	 * @see org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator#evaluate(org.openmrs.module.reporting.cohort.definition.CohortDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		ObsInLastVisitCohortDefinition cd = (ObsInLastVisitCohortDefinition) cohortDefinition;

		VisitsForPatientDataDefinition visitsDef = new VisitsForPatientDataDefinition();
		visitsDef.setWhich(TimeQualifier.LAST);
		PatientData visitsData = Context.getService(PatientDataService.class).evaluate(visitsDef, context);

		ObsForPersonDataDefinition obssDef = new ObsForPersonDataDefinition();
		obssDef.setQuestion(cd.getQuestion());
		obssDef.setWhich(TimeQualifier.ANY);
		PersonData obssData = Context.getService(PersonDataService.class).evaluate(obssDef, context);

		Set<Integer> patientIds = new HashSet<Integer>();

		for (Integer ptId : context.getBaseCohort().getMemberIds()) {
			Visit lastVisit = (Visit) visitsData.getData().get(ptId);
			List<Obs> allObss = (List<Obs>) obssData.getData().get(ptId);

			if (lastVisit != null && allObss != null) {
				for (Obs obs : allObss) {
					if (obsDuringVisit(obs, lastVisit)) {
						patientIds.add(ptId);
						break;
					}
				}
			}
		}

		return new EvaluatedCohort(new Cohort(patientIds), cd, context);
	}

	/**
	 * Helper method to check if an obs occurred during a visit
	 * @param obs the obs
	 * @param visit the visit
	 * @return true if obs occurred during the visit
	 */
	protected boolean obsDuringVisit(Obs obs, Visit visit) {
		return OpenmrsUtil.compare(obs.getObsDatetime(), visit.getStartDatetime()) >= 0
				&& OpenmrsUtil.compareWithNullAsLatest(obs.getObsDatetime(), visit.getStopDatetime()) < 0;
	}
}