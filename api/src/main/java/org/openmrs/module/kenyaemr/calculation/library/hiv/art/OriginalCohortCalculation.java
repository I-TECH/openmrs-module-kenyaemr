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
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculates the patient arv start date in this facility
 */
public class OriginalCohortCalculation extends AbstractPatientCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
										 PatientCalculationContext context) {

		Set<Integer> artStartDate = CalculationUtils.patientsThatPass(calculate(new InitialArtStartDateCalculation(), cohort, context));
		//Set<Integer> transferIns = CalculationUtils.patientsThatPass(calculate(new StartedArtAtTransferingFacilityCalculation(), cohort, context));
		CalculationResultMap initialArtStartDate = calculate(new InitialArtStartDateCalculation(), cohort, context);
		CalculationResultMap result = new CalculationResultMap();
		for (Integer ptId : cohort) {
			Date arvStartDate = null;
			if((artStartDate.contains(ptId))){
					arvStartDate = EmrCalculationUtils.datetimeResultForPatient(initialArtStartDate,ptId);
			}
				result.put(ptId, new SimpleResult(arvStartDate, this));
		}
		return  result;

	}
}
