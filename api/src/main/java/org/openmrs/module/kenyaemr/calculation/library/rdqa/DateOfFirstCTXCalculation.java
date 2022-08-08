/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.rdqa;

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
 * Returns the date of the first CTX dispensed
 */
public class DateOfFirstCTXCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();
		CalculationResultMap medOrders = Calculations.firstObs(Dictionary.getConcept(Dictionary.MEDICATION_ORDERS), cohort, context);
		CalculationResultMap ctxProphylaxisDispenced = Calculations.firstObs(Dictionary.getConcept(Dictionary.COTRIMOXAZOLE_DISPENSED), cohort, context);

		if (medOrders == null && ctxProphylaxisDispenced == null){
			return ret;
		}

		Concept yes = Dictionary.getConcept(Dictionary.YES);
		Concept dapsone = Dictionary.getConcept(Dictionary.DAPSONE);
		Concept ctx = Dictionary.getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM);

		for (Integer ptId : cohort) {
			Date firstCtx = null;
			Obs medOrdersObs= EmrCalculationUtils.obsResultForPatient(medOrders, ptId);
			Obs ctxProphylaxisDispencedObs= EmrCalculationUtils.obsResultForPatient(ctxProphylaxisDispenced, ptId);
			if(ctxProphylaxisDispencedObs != null){
				if(ctxProphylaxisDispencedObs.getValueCoded() != null && ctxProphylaxisDispencedObs.getValueCoded().equals(yes)) {
					firstCtx = ctxProphylaxisDispencedObs.getObsDatetime();
				}
			}
			if((medOrdersObs != null && medOrdersObs.getValueCoded().equals(ctx)) || (medOrdersObs != null && medOrdersObs.getValueCoded().equals(dapsone))) {
				firstCtx = medOrdersObs.getObsDatetime();
			}

			ret.put(ptId, new SimpleResult(firstCtx, this));

		}

		return ret;
	}


}