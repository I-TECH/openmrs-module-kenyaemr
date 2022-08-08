/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
public class OnOriginalFirstLineArtCalculation extends AbstractPatientCalculation {

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
			boolean onOrigFirstLine = false;

			SimpleResult initialArvsResult = (SimpleResult) initialArvs.get(ptId);
			SimpleResult currentArvsResult = (SimpleResult) currentArvs.get(ptId);

			if (initialArvsResult != null && currentArvsResult != null) {
				RegimenOrder initialRegimen = (RegimenOrder) initialArvsResult.getValue();
				RegimenOrder currentRegimen = (RegimenOrder) currentArvsResult.getValue();

				onOrigFirstLine = initialRegimen.hasSameDrugs(currentRegimen) && EmrCalculationUtils.regimenInGroup(currentRegimen, "ARV", "adult-first");
			}

			ret.put(ptId, new BooleanResult(onOrigFirstLine, this, context));
		}
		return ret;
    }
}