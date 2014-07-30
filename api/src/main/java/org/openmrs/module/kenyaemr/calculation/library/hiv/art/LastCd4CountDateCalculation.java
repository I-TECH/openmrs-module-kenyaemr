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
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastCd4CountCalculation;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculates the last date when cd4 count/cd4 % was taken
 */
public class LastCd4CountDateCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
										 PatientCalculationContext context) {

		CalculationResultMap lastCd4 = calculate(new LastCd4CountCalculation(), cohort, context);
		CalculationResultMap result = new CalculationResultMap();
		for (Integer ptId : cohort) {
			Obs  cd4Obs = EmrCalculationUtils.obsResultForPatient(lastCd4, ptId);
			Date lastCd4Date = null;
			if(cd4Obs != null){
				lastCd4Date = cd4Obs.getObsDatetime();
			}
			result.put(ptId, new SimpleResult(lastCd4Date, this));
		}
		return  result;
	}
}
