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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * Calculates whether a patient's transfer has been verified or not
 */
public class TransferOutVerificationCalculation extends AbstractPatientCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(Collection,
	 *      Map, PatientCalculationContext)
	 */
	protected static final Log log = LogFactory.getLog(TransferOutVerificationCalculation.class);
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
										 PatientCalculationContext context) {

		Concept trfOutVerified = Dictionary.getConcept(Dictionary.TRF_OUT_VERIFIED);
		Concept discontinueQuestion = Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION);
		Concept transferOut = Dictionary.getConcept(Dictionary.TRANSFERRED_OUT);
		CalculationResultMap trfOutVerifiedResults = Calculations.lastObs(trfOutVerified, cohort, context);
		CalculationResultMap discontinueObs = Calculations.lastObs(discontinueQuestion, cohort, context);

		CalculationResultMap result = new CalculationResultMap();
		for(int ptId : cohort){
			Concept isTrfVerified = EmrCalculationUtils.codedObsResultForPatient(trfOutVerifiedResults, ptId);
			Obs discoReason = EmrCalculationUtils.obsResultForPatient(discontinueObs, ptId);
			Concept value = null;
			if(isTrfVerified != null && discoReason.getValueCoded().equals(transferOut)){
				value = isTrfVerified;
			}

			result.put(ptId, new SimpleResult(value, this));

		}

		return  result;
	}
}
