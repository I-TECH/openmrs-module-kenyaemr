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
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculates whether a patient is a transfer in date
 */
public class TransferInDateCalculation extends AbstractPatientCalculation {
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
										 PatientCalculationContext context) {

		Concept transferInDate = Dictionary.getConcept(Dictionary.TRANSFER_IN_DATE);
		Concept transferInStatus = Dictionary.getConcept(Dictionary.TRANSFER_IN);
		CalculationResultMap transferInStatusResults = Calculations.lastObs(transferInStatus,cohort,context);

		CalculationResultMap transferInDateResults = Calculations.lastObs(transferInDate, cohort, context);

		CalculationResultMap result = new CalculationResultMap();
		for (Integer ptId : cohort) {
			Date transferInDateValue = null;
			Date tDate = EmrCalculationUtils.datetimeObsResultForPatient(transferInDateResults, ptId);
			Obs statusObs = EmrCalculationUtils.obsResultForPatient(transferInStatusResults, ptId);
			if(tDate != null){
				transferInDateValue = tDate;
			}
			else if(statusObs != null && statusObs.getValueCoded().equals(Dictionary.getConcept(Dictionary.YES))) {
				transferInDateValue = statusObs.getObsDatetime();
			}

			result.put(ptId, new SimpleResult(transferInDateValue, this));

		}
		return result;
	}
}
