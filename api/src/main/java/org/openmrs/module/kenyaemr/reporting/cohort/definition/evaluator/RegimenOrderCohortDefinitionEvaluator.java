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
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.reporting.cohort.definition.RegimenOrderCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.data.patient.PatientData;
import org.openmrs.module.reporting.data.patient.definition.DrugOrdersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Evaluator for regimen orders based cohorts
 */
@Handler(supports = RegimenOrderCohortDefinition.class)
public class RegimenOrderCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

	/**
	 * @see org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator#evaluate(org.openmrs.module.reporting.cohort.definition.CohortDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
	 */
	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) throws EvaluationException {

		RegimenOrderCohortDefinition cd = (RegimenOrderCohortDefinition) cohortDefinition;

		DrugOrdersForPatientDataDefinition drugOrdersForPatientDataDefinition = new DrugOrdersForPatientDataDefinition();
		drugOrdersForPatientDataDefinition.setDrugConceptSetsToInclude(Arrays.asList(cd.getMasterConceptSet()));
		drugOrdersForPatientDataDefinition.setActiveOnDate(cd.getOnDate());

		Set<Integer> patientIds = new HashSet<Integer>();
		PatientData patientData = Context.getService(PatientDataService.class).evaluate(drugOrdersForPatientDataDefinition, context);

		for (Map.Entry<Integer, Object> d :patientData.getData().entrySet()) {

			Set<DrugOrder> drugOrderSet = new HashSet<DrugOrder>((Collection<? extends DrugOrder>) d.getValue());

			Set<Concept> conceptSet = new HashSet<Concept>();

			for (DrugOrder drugOrder: drugOrderSet) {
				conceptSet.add(drugOrder.getConcept());
				if ((cd.getConceptSet().size() == conceptSet.size()) && (conceptSet.containsAll(cd.getConceptSet()))) {
					patientIds.add(d.getKey());
				}
			}
		}
		return new EvaluatedCohort(new Cohort(patientIds), cd, context);
	}
}