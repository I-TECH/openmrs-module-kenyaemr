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
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculate the first date a client was started on arvs
 */
public class DateARV1Calculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
										 PatientCalculationContext context) {

		CalculationResultMap patientsStartedAtThisFacility = calculate(new OriginalCohortCalculation(), cohort, context);
		CalculationResultMap transferInPatients = Calculations.lastObs(Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_TREATMENT_START_DATE), cohort, context);

		CalculationResultMap result = new CalculationResultMap();
		for (Integer ptId : cohort) {
			Date dateArv1 = null;
			Date startedHere = EmrCalculationUtils.datetimeResultForPatient(patientsStartedAtThisFacility, ptId);
			Obs transferIns = EmrCalculationUtils.obsResultForPatient(transferInPatients, ptId);
			if(startedHere != null){
				dateArv1 = startedHere;
			}

			if((transferInPatients.containsKey(ptId)) && (transferIns != null)){
				dateArv1 = transferIns.getValueDate();
			}
			result.put(ptId, new SimpleResult(dateArv1, this));

		}

		return  result;
	}
}
