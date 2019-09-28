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
