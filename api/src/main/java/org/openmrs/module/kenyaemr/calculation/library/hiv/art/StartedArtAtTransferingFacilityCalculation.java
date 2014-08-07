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
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculates those patients who started ART while at the transferring facility
 */
public class StartedArtAtTransferingFacilityCalculation extends AbstractPatientCalculation {
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 * java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
										 PatientCalculationContext context) {

		CalculationResultMap startedArtFromTransferringFacility = Calculations.lastObs(Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_TREATMENT_START_DATE), cohort, context);

		CalculationResultMap result = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean startedArt = false;
			Date dateStartedART = EmrCalculationUtils.datetimeObsResultForPatient(startedArtFromTransferringFacility, ptId);
			if (dateStartedART != null) {
				startedArt = true;
			}
			result.put(ptId, new BooleanResult(startedArt, this));
		}
		return  result;

	}
}
