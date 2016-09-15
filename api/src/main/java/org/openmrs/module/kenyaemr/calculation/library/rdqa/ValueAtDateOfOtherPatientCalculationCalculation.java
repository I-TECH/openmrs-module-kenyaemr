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
package org.openmrs.module.kenyaemr.calculation.library.rdqa;

import org.openmrs.Concept;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.ObsResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Returns value of calculation at date of other patient calculation
 */
public class ValueAtDateOfOtherPatientCalculationCalculation extends AbstractPatientCalculation {

	private PatientCalculation baseCalculation;
	private Concept question;

	public ValueAtDateOfOtherPatientCalculationCalculation(PatientCalculation baseCalculation, Concept question) {
		this.baseCalculation = baseCalculation;
		this.question = question;
	}

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		CalculationResultMap dataForOtherCalculation = evaluateBaseCalculation(cohort, context);
		CalculationResultMap questionEvaluationData = evaluateQuestion(cohort, parameterValues, context);

		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptid : cohort) {
			Double ans = null;
			SimpleResult baseResult = (SimpleResult) dataForOtherCalculation.get(ptid);

			if (baseResult !=null) {
				Date baseDate = (Date) baseResult.getValue();

				List<ObsResult> qObs = extractObsFromQuestionResultMap(questionEvaluationData, ptid);

				for (ObsResult obs : qObs) {
					if (baseDate != null) {
						if (obs != null) {
							if (obs.getValue().getObsDatetime().equals(baseDate)) {
								ans = obs.getValue().getValueNumeric();

							}
						}
					}
				}
			}
			ret.put(ptid, new SimpleResult(ans, this));
		}

		return ret;
	}

	public PatientCalculation getBaseCalculation() {
		return baseCalculation;
	}

	public void setBaseCalculation(PatientCalculation baseCalculation) {
		this.baseCalculation = baseCalculation;
	}

	public Concept getQuestion() {
		return question;
	}

	public void setQuestion(Concept question) {
		this.question = question;
	}

	private CalculationResultMap evaluateBaseCalculation(Collection<Integer> cohort, PatientCalculationContext context){
		return calculate(getBaseCalculation(), cohort, context);
	}

	private CalculationResultMap evaluateQuestion(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context){
		ObsForPersonDataDefinition definition = new ObsForPersonDataDefinition("Evaluate Question Concept");
		definition.setWhich(TimeQualifier.ANY);
		definition.setQuestion(getQuestion());
		return CalculationUtils.evaluateWithReporting(definition, cohort, parameterValues, null, context);
	}

	private List<ObsResult> extractObsFromQuestionResultMap(CalculationResultMap calculationResultMap, Integer ptId){
		List<ObsResult> obs = new ArrayList<ObsResult>();
		if (calculationResultMap !=null){
			ListResult result = (ListResult) calculationResultMap.get(ptId);
			obs = (List<ObsResult>) result.getValue();
		}
		return obs;
	}



}
