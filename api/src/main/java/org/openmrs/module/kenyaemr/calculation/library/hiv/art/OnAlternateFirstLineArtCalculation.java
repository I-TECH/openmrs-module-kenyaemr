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
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;

import java.util.Collection;
import java.util.Map;

/**
 * Calculates whether patients are on alternative first-line ART regimens, i.e. have they changed regimen
 */
public class OnAlternateFirstLineArtCalculation extends AbstractPatientCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		// Get initial and current ART regimen of each patient
		CalculationResultMap initialArvs = calculate(new InitialArtRegimenCalculation(), cohort, context);
		CalculationResultMap currentArvs = calculate(new CurrentArtRegimenCalculation(), cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean onAltFirstLine = false;

			SimpleResult initialArvsResult = (SimpleResult) initialArvs.get(ptId);
			SimpleResult currentArvsResult = (SimpleResult) currentArvs.get(ptId);

			if (initialArvsResult != null && currentArvsResult != null) {
				RegimenOrder initialRegimen = (RegimenOrder) initialArvsResult.getValue();
				RegimenOrder currentRegimen = (RegimenOrder) currentArvsResult.getValue();

				onAltFirstLine = !initialRegimen.hasSameDrugs(currentRegimen) && EmrCalculationUtils.regimenInGroup(currentRegimen, "ARV", "adult-first");
			}

			ret.put(ptId, new BooleanResult(onAltFirstLine, this, context));
		}
		return ret;
    }
}