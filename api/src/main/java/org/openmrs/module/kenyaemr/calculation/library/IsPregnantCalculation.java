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

package org.openmrs.module.kenyaemr.calculation.library;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Calculates the recorded pregnancy status of patients
 */
public class IsPregnantCalculation extends BaseEmrCalculation implements PatientFlagCalculation {

	@Override
	public String getFlagMessage() {
		return "Pregnant";
	}

    /**
	 * Evaluates the calculation
     * @should calculate null for deceased patients
	 * @should calculate null for patients with no recorded status
	 * @should calculate last recorded pregnancy status for all patients
     */
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Set<Integer> alive = alivePatients(cohort, context);

		Concept yes = getConcept(Dictionary.YES);
		CalculationResultMap pregStatusObss = Calculations.lastObs(getConcept(Dictionary.PREGNANCY_STATUS), alive, context);
		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptId : cohort) {
			BooleanResult result = null;
			Obs pregStatusObs = EmrCalculationUtils.obsResultForPatient(pregStatusObss, ptId);

			if (pregStatusObs != null) {
				result = new BooleanResult(pregStatusObs.getValueCoded().equals(yes), this);
			}

			ret.put(ptId, result);
		}

		return ret;
    }
}