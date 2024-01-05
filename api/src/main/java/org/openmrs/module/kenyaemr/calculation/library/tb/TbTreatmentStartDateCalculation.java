/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.tb;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils.daysSince;

/**
 * Calculate results of patient who started tb treatment n months after
 */
public class TbTreatmentStartDateCalculation extends AbstractPatientCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
		//get the parameter from the cohort definition
		Integer months = (parameterValues != null && parameterValues.containsKey("months")) ? (Integer) parameterValues.get("months") : null;

		Set<Integer> alive = Filters.alive(cohort,context);
		//get the date of tb start as a calculation map
		CalculationResultMap tbStartDate = Calculations.lastObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_DRUG_TREATMENT_START_DATE), alive, context);

		CalculationResultMap ret = new CalculationResultMap();
		for(int ptId : cohort){
			boolean results4Months = false;
			Date dateStartedTb = EmrCalculationUtils.datetimeObsResultForPatient(tbStartDate, ptId);
			if(dateStartedTb != null && (daysSince(dateStartedTb, context) >= months * 30)) {
				results4Months = true;
			}
			ret.put(ptId, new BooleanResult(results4Months, this, context));
		}
		return ret;

	}
}
