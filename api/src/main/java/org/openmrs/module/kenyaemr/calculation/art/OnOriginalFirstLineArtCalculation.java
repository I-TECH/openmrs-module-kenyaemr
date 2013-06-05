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

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;

import java.util.Collection;
import java.util.Map;

/**
 * Calculates whether patients are on alternative first-line ART regimens, i.e. have they changed regimen
 */
public class OnOriginalFirstLineArtCalculation extends BaseEmrCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @should return null for patients who have never started ARVs
	 * @should return null for patients who aren't currently on ARVs
	 * @should return whether patients have changed regimens
	 */
	@Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> arg1, PatientCalculationContext context) {

		// Get initial and current ART regimen of each patient
		CalculationResultMap initialArvs = calculate(new InitialArtRegimenCalculation(), cohort, context);
		CalculationResultMap currentArvs = calculate(new CurrentArtRegimenCalculation(), cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			BooleanResult result = null;
			SimpleResult initialArvsResult = (SimpleResult) initialArvs.get(ptId);
			SimpleResult currentArvsResult = (SimpleResult) currentArvs.get(ptId);

			if (initialArvsResult != null && currentArvsResult != null) {
				RegimenOrder initialRegimen = (RegimenOrder) initialArvsResult.getValue();
				RegimenOrder currentRegimen = (RegimenOrder) currentArvsResult.getValue();

				boolean isAltFirstLine = initialRegimen.hasSameDrugs(currentRegimen) && CalculationUtils.regimenInGroup(currentRegimen, "ARV", "adult-first");
				result = new BooleanResult(isAltFirstLine, this, context);
			}

			ret.put(ptId, result);
		}
		return ret;
    }
}