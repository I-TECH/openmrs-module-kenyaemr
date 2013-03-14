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

package org.openmrs.module.kenyaemr.calculation.art;

import java.util.Collection;
import java.util.Map;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.calculation.BaseAlertCalculation;
import org.openmrs.module.kenyaemr.calculation.BooleanResult;

/**
 * Calculates whether patients are on ART
 */
public class OnArtCalculation extends BaseAlertCalculation {

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation#getShortMessage()
	 */
	@Override
	public String getShortMessage() {
		return "Patients on ART";
	}

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.BaseAlertCalculation#getAlertMessage()
	 */
	@Override
	public String getAlertMessage() {
		return "On ART";
	}

	@Override
	public String[] getTags() {
		return new String[] { "hiv" };
	}
	
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
	                                     PatientCalculationContext context) {

		// Get active ART regimen of each patient
		CalculationResultMap patientArvs = calculate(new CurrentArtRegimenCalculation(), cohort, context);

		// Return only whether or not patient is on ARTs
		CalculationResultMap ret = new CalculationResultMap();
		for (Map.Entry<Integer, CalculationResult> e : patientArvs.entrySet()) {
			boolean onART = e.getValue() != null && !e.getValue().isEmpty();
			ret.put(e.getKey(), new BooleanResult(onART, this));
		}
		return ret;
	}
}