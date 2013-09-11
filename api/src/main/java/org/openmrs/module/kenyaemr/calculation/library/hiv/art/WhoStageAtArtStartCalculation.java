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

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.util.EmrUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * Calculates a patient's WHO stage on the date they started ARTs
 */
public class WhoStageAtArtStartCalculation extends BaseEmrCalculation {
	
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Concept pregnancyStatus = getConcept(Dictionary.CURRENT_WHO_STAGE);
		CalculationResultMap artStartDates = calculate(new InitialArtStartDateCalculation(), cohort, context);

		// Return the earliest of the two
		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			SimpleResult result = null;
			Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDates, ptId);

			if (artStartDate != null) {
				CalculationResultMap pregStatusObss = Calculations.lastObsOnOrBefore(pregnancyStatus, artStartDate, Collections.singleton(ptId), context);
				Obs whoStageObs = EmrCalculationUtils.obsResultForPatient(pregStatusObss, ptId);

				if (whoStageObs != null) {
					result = new SimpleResult(EmrUtils.whoStage(whoStageObs.getValueCoded()), this);
				}
			}

			ret.put(ptId, result);
		}
		return ret;
	}
}