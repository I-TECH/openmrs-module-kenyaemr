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

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;

import java.util.Collection;
import java.util.Map;

/**
 * Calculates whether patients are on second-line ART regimens
 */
public class OnSecondLineArtCalculation extends BaseEmrCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @should return null for patients who have never started ARVs
	 * @should return whether patients are currently taking a second-line regimen
	 */
	@Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> arg1, PatientCalculationContext context) {

		// Get active ART regimen of each patient
		CalculationResultMap currentArvs = calculate(new CurrentArtRegimenCalculation(), cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			BooleanResult result = null;
			SimpleResult currentArvResult = (SimpleResult) currentArvs.get(ptId);

			if (currentArvResult != null) {
				RegimenOrder currentRegimen = (RegimenOrder) currentArvResult.getValue();

				boolean isSecondLine = EmrCalculationUtils.regimenInGroup(currentRegimen, "ARV", "adult-second");
				result = new BooleanResult(isSecondLine, this, context);
			}

			ret.put(ptId, result);
		}
		return ret;
    }
}