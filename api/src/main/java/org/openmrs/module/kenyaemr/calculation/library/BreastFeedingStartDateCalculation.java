/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculates the recorded Breastfeeding start date for patients
 */
public class BreastFeedingStartDateCalculation extends AbstractPatientCalculation  {
    /**
	 * Evaluates the calculation
     * @should calculate null for deceased patients
	 * @should calculate null for patients with no recorded breast feeding status
	 * @should calculate last recorded breastfeeding Date for all patients on PNC
	 * @should calculate last recorded breastfeeding Date for all patients on Hiv Greencard
     */
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Set<Integer> aliveAndFemale = Filters.female(Filters.alive(cohort, context), context);

		Concept ExclusiveBreastFeeding = Dictionary.getConcept(Dictionary.BREASTFED_EXCLUSIVELY);
		Concept MixedBreastFeeding = Dictionary.getConcept(Dictionary.MIXED_FEEDING);
		Concept YES = Dictionary.getConcept(Dictionary.YES);
		Concept BreastFeedingHivFollowupQuestion = Context.getConceptService().getConcept(5632);
		CalculationResultMap infantFeedingStatusObs = Calculations.lastObs(Dictionary.getConcept(Dictionary.INFANT_FEEDING_METHOD), aliveAndFemale, context);
		CalculationResultMap breastFeedingFollowupObs = Calculations.lastObs(BreastFeedingHivFollowupQuestion, aliveAndFemale, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			Date breastFeedingStarDate = null;

			Obs feedingStatusObs = EmrCalculationUtils.obsResultForPatient(infantFeedingStatusObs, ptId);
			Obs bfHivFollowupStatusObs = EmrCalculationUtils.obsResultForPatient(breastFeedingFollowupObs, ptId);

			if (feedingStatusObs != null && (feedingStatusObs.getValueCoded().equals(ExclusiveBreastFeeding) || feedingStatusObs.getValueCoded().equals(MixedBreastFeeding)) ) {
				breastFeedingStarDate = feedingStatusObs.getObsDatetime();
			}
			if (bfHivFollowupStatusObs != null && (bfHivFollowupStatusObs.getValueCoded().equals(YES)) ) {
				breastFeedingStarDate = bfHivFollowupStatusObs.getObsDatetime();
			}

			ret.put(ptId, new SimpleResult(breastFeedingStarDate, this));
		}

		return ret;
    }
}