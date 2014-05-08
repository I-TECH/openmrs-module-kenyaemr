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

package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.ObsResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Calculates the obs which triggered each patient to become eligible for ART
 */
public class EligibleForArtTriggerCalculation extends AbstractPatientCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @should calculate the obs which triggered a patient to become eligible for ART
	 * @should return null for patients who have never been eligible for ART
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
	                                     PatientCalculationContext context) {

		// Gather all relevant obs that can be trigger events
		CalculationResultMap confirmedPositives = Calculations.allObs(Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS), cohort, context);
		CalculationResultMap whoStages = Calculations.allObs(Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE), cohort, context);
		CalculationResultMap cdCounts = Calculations.allObs(Dictionary.getConcept(Dictionary.CD4_COUNT), cohort, context);
		CalculationResultMap cdPercents = Calculations.allObs(Dictionary.getConcept(Dictionary.CD4_PERCENT), cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			// Extract relevant obs for this patient
			List<Obs> confirmedPosObss = CalculationUtils.extractResultValues((ListResult) confirmedPositives.get(ptId));
			List<Obs> whoStageObss = CalculationUtils.extractResultValues((ListResult) whoStages.get(ptId));
			List<Obs> cdCountObss = CalculationUtils.extractResultValues((ListResult) cdCounts.get(ptId));
			List<Obs> cdPercentObss = CalculationUtils.extractResultValues((ListResult) cdPercents.get(ptId));

			// Combine into one list
			List<Obs> allObss = new ArrayList<Obs>();
			allObss.addAll(confirmedPosObss);
			allObss.addAll(whoStageObss);
			allObss.addAll(cdCountObss);
			allObss.addAll(cdPercentObss);

			// Filter to only those which are triggers
			List<Obs> allTriggerObss = extractTriggers(allObss);

			// Find the earliest trigger obs
			Obs eligibilityTriggerObs = findEarliestTrigger(allTriggerObss);

			ret.put(ptId, eligibilityTriggerObs != null ? new ObsResult(eligibilityTriggerObs, this) : null);
		}
		return ret;
	}

	/**
	 * Extracts all obs from the given list that can be triggers for becoming eligible for ART
	 * @param obss the list of all obs
	 * @return the obs which could be triggers
	 */
	private List<Obs> extractTriggers(List<Obs> obss) {
		List<Obs> triggerObs = new ArrayList<Obs>();

		for (Obs o : obss) {
			boolean isTrigger = false;
			Age ageAtObs = new Age(o.getPerson().getBirthdate(), o.getObsDatetime());

			// Was patient < 24 months at HIV+ confirmation
			if (o.getConcept().equals(Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS))) {
				Date confirmationDate = o.getValueDate();
				Age ageAtConfirmation = new Age(o.getPerson().getBirthdate(), confirmationDate);

				if (ageAtConfirmation.getFullMonths() < 24) {
					isTrigger = true;
				}
			}
			// Did patient become WHO stage 3 or 4?
			else if (o.getConcept().equals(Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE))) {
				if (EmrUtils.whoStage(o.getValueCoded()) >= 3) {
					isTrigger = true;
				}
			}
			// Did patient's CD4 count drop below: 1000 (25-59 months) or 500 (5-12 years) or 350 (everyone else)
			else if (o.getConcept().equals(Dictionary.getConcept(Dictionary.CD4_COUNT))) {
				Double cd4 = o.getValueNumeric();

				if ((ageAtObs.getFullMonths() < 60 && cd4 < 1000) || (ageAtObs.getFullYears() <= 12 && cd4 < 500) || cd4 < 350) {
					isTrigger = true;
				}
			}
			else if (o.getConcept().equals(Dictionary.getConcept(Dictionary.CD4_PERCENT))) {
				Double cd4Pc = o.getValueNumeric();

				if ((ageAtObs.getFullMonths() < 60 && cd4Pc < 25) || (ageAtObs.getFullYears() <= 12 && cd4Pc < 20)) {
					isTrigger = true;
				}
			}

			if (isTrigger) {
				triggerObs.add(o);
			}
		}

		return triggerObs;
	}

	/**
	 * Finds the earliest trigger obs in the given list
	 * @param triggers
	 * @return
	 */
	private Obs findEarliestTrigger(List<Obs> triggers) {
		Date earliestDate = null;
		Obs earliest = null;

		for (Obs trigger : triggers) {
			// If obs is for HIV diagnosis date, then use obs value rather than obs date
			Date triggerDate = trigger.getConcept().equals(Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS))
					? trigger.getValueDate()
					: trigger.getObsDatetime();

			if (earliest == null || OpenmrsUtil.compare(triggerDate, earliestDate) < 0) {
				earliestDate = triggerDate;
				earliest = trigger;
			}
		}

		return earliest;
	}
}