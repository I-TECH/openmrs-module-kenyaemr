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

package org.openmrs.module.kenyaemr.calculation.library.art;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.calculation.*;
import org.openmrs.module.kenyaemr.util.KenyaEmrUtils;
import org.openmrs.module.reporting.common.Age;

/**
 *
 */
public class EligibleForArtCalculation extends BaseFlagCalculation {

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.BaseFlagCalculation#getFlagMessage()
	 */
	@Override
	public String getFlagMessage() {
		return "Eligible for ART";
	}
	
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @should calculate eligibility
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
	                                     PatientCalculationContext context) {

		// only applies to patients in the HIV program
		Program hivProgram = Metadata.getProgram(Metadata.HIV_PROGRAM);
		Set<Integer> inHivProgram = CalculationUtils.patientsThatPass(activeEnrollment(hivProgram, cohort, context));
		
		// need to exclude those on ART already
		Set<Integer> onArt = CalculationUtils.patientsThatPass(calculate(new OnArtCalculation(), cohort, context));
		
		CalculationResultMap ages = ages(cohort, context);
		
		CalculationResultMap lastWhoStage = calculate(new LastWhoStageCalculation(), cohort, context);
		CalculationResultMap lastCd4 = calculate(new LastCd4CountCalculation(), cohort, context);
		CalculationResultMap lastCd4Percent = calculate(new LastCd4PercentageCalculation(), cohort, context);
		
		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean eligible = false;
			if (inHivProgram.contains(ptId) && !onArt.contains(ptId)) {
				int ageInMonths = ((Age) ages.get(ptId).getValue()).getFullMonths();
				Double cd4 = CalculationUtils.numericObsResultForPatient(lastCd4, ptId);
				Double cd4Percent = CalculationUtils.numericObsResultForPatient(lastCd4Percent, ptId);
				Integer whoStage = KenyaEmrUtils.whoStage(CalculationUtils.codedObsResultForPatient(lastWhoStage, ptId));
				eligible = isEligible(ageInMonths, cd4, cd4Percent, whoStage);
			}
			ret.put(ptId, new BooleanResult(eligible, this));
		}
		return ret;
	}

    private boolean isEligible(int ageInMonths, Double cd4, Double cd4Percent, Integer whoStage) {
		if (ageInMonths < 24) {
			return true;
		} else if (ageInMonths < 60) { // 24-59 months
			if (whoStage != null && (whoStage == 3 || whoStage == 4)) {
				return true;
			}
			if (cd4Percent != null && cd4Percent < 25) {
				return true;
			}
			if (cd4 != null && cd4 < 1000) {
				return true;
			}
		} else if (ageInMonths < 155) { // 5-12 years
			if (whoStage != null && (whoStage == 3 || whoStage == 4)) {
				return true;
			}
			if (cd4Percent != null && cd4Percent < 20) {
				return true;
			}
			if (cd4 != null && cd4 < 500) {
				return true;
			}
		} else { // 13+ years
			if (whoStage != null && (whoStage == 3 || whoStage == 4)) {
				return true;
			}
			if (cd4 != null && cd4 < 350) {
				return true;
			}
		}
		return false;
	}
}