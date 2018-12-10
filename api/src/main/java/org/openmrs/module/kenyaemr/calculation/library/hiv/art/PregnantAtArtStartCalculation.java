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

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
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