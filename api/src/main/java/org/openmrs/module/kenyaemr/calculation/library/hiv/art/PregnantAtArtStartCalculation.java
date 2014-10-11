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
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether a patient was pregnant on the date they started ARTs
 */
public class PregnantAtArtStartCalculation extends AbstractPatientCalculation {
	
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Set<Integer> female = Filters.female(cohort, context);

		Concept yes = Dictionary.getConcept(Dictionary.YES);
		Concept pregnancyStatus = Dictionary.getConcept(Dictionary.PREGNANCY_STATUS);
		CalculationResultMap artStartDates = calculate(new InitialArtStartDateCalculation(), female, context);
		CalculationResultMap pregnancyObss = Calculations.allObs(pregnancyStatus, female, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean result = false;

			if (female.contains(ptId)) {
				Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDates, ptId);
				ListResult pregObssResult = (ListResult) pregnancyObss.get(ptId);

				if (artStartDate != null && pregObssResult != null && !pregObssResult.isEmpty()) {
					List<Obs> pregnancyStatuses = CalculationUtils.extractResultValues(pregObssResult);
					Obs lastBeforeArtStart = EmrCalculationUtils.findLastOnOrBefore(pregnancyStatuses, artStartDate);

					if (lastBeforeArtStart != null && lastBeforeArtStart.getValueCoded().equals(yes)) {
						result = true;
					}
				}
			}

			ret.put(ptId, new BooleanResult(result, this));
		}
		return ret;
	}
}