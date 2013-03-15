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

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.CalculationUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * Calculates whether a patient was pregnant on the date they started ARTs
 */
public class PregnantAtArtStartCalculation extends BaseEmrCalculation {
	
	/**
	 * @see org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation#getName()
	 */
	@Override
	public String getName() {
		return "Pregnant On ART Start Date";
	}

	@Override
	public String[] getTags() {
		return new String[] { "hiv" };
	}
	
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Concept yes = getConcept(MetadataConstants.YES_CONCEPT_UUID);
		Concept pregnancyStatus = getConcept(MetadataConstants.PREGNANCY_STATUS_CONCEPT_UUID);
		CalculationResultMap artStartDates = calculate(new InitialArtStartDateCalculation(), cohort, context);

		// Return the earliest of the two
		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			BooleanResult result = null;
			Date artStartDate = CalculationUtils.datetimeResultForPatient(artStartDates, ptId);

			if (artStartDate != null) {
				CalculationResultMap pregStatusObss = lastObsOnOrBeforeDate(pregnancyStatus, artStartDate, Collections.singleton(ptId), context);
				Obs pregStatusObs = CalculationUtils.obsResultForPatient(pregStatusObss, ptId);

				if (pregStatusObs != null) {
					result = new BooleanResult(pregStatusObs.getValueCoded().equals(yes), this);
				}
			}

			ret.put(ptId, result);
		}
		return ret;
	}
}