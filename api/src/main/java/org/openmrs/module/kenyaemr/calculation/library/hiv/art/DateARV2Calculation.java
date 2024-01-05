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
					if (o.getDateActivated() != null) {
						secondLineStartDate = o.getDateActivated();
						break;
					}
				}
			}
			result.put(ptId, new SimpleResult(secondLineStartDate, this));
		}
		return result;
	}
}