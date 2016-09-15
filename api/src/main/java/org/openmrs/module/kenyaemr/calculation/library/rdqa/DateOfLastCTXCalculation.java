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
 * Returns the date of the last CTX dispensed
 */
public class DateOfLastCTXCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();
		CalculationResultMap medOrders = Calculations.lastObs(Dictionary.getConcept(Dictionary.MEDICATION_ORDERS), cohort, context);
		CalculationResultMap ctxProphylaxisDispenced = Calculations.lastObs(Dictionary.getConcept(Dictionary.COTRIMOXAZOLE_DISPENSED), cohort, context);

		if (medOrders == null && ctxProphylaxisDispenced == null){
			return ret;
		}

		Concept yes = Dictionary.getConcept(Dictionary.YES);
		Concept dapsone = Dictionary.getConcept(Dictionary.DAPSONE);
		Concept ctx = Dictionary.getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM);

		for (Integer ptId : cohort) {
			Date lastCtx = null;
			Obs medOrdersObs= EmrCalculationUtils.obsResultForPatient(medOrders, ptId);
			Obs ctxProphylaxisDispencedObs= EmrCalculationUtils.obsResultForPatient(ctxProphylaxisDispenced, ptId);
			if(ctxProphylaxisDispencedObs != null){
				if(ctxProphylaxisDispencedObs.getValueCoded() != null && ctxProphylaxisDispencedObs.getValueCoded().equals(yes)) {
					lastCtx = ctxProphylaxisDispencedObs.getObsDatetime();
				}
			}
			if((medOrdersObs != null && medOrdersObs.getValueCoded().equals(ctx)) || (medOrdersObs != null && medOrdersObs.getValueCoded().equals(dapsone))) {
				lastCtx = medOrdersObs.getObsDatetime();
			}

			ret.put(ptId, new SimpleResult(lastCtx, this));

		}

		return ret;
	}


}