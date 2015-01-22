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

import org.openmrs.DrugOrder;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Date a patient changed to second line of arv
 */
public class DateARV2Calculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
										 PatientCalculationContext context) {

		Set<Integer> patientsOnSecondLine = CalculationUtils.patientsThatPass(calculate(new OnSecondLineArtCalculation(), cohort, context));
		CalculationResultMap currentArvs = calculate(new CurrentArtRegimenCalculation(), cohort, context);
		CalculationResultMap result = new CalculationResultMap();
		for (Integer ptId : cohort) {
			Date secondLineStartDate = null;
			SimpleResult currentArvResult = (SimpleResult) currentArvs.get(ptId);
			if (currentArvResult != null && patientsOnSecondLine.contains(ptId)) {
				RegimenOrder currentRegimen = (RegimenOrder) currentArvResult.getValue();
					Set<DrugOrder> drugs = currentRegimen.getDrugOrders();
					for (DrugOrder o : drugs) {
						if (o.getStartDate() != null) {
							secondLineStartDate = o.getStartDate();
							break;
						}
					}
			}
			result.put(ptId, new SimpleResult(secondLineStartDate, this));
		}
		return result;
	}
}
