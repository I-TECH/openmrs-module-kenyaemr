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
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 22/01/15.
 */
public class CurrentARTStartDateCalculation extends AbstractPatientCalculation {
	/**
	 * Evaluates a calculation for a cohort of patients taking into consideration any specified
	 * parameter values and contextual information. <br/>
	 * <b>NOTE:</b> implementations are not expected to do sophisticated memory management, so if you
	 * want to evaluate a calculation on a very large number of patients, you should use one of the
	 * evaluate methods in {@link PatientCalculationService} instead, since these will run the calculation
	 * on manageable batches.
	 *
	 * @param cohort          patientIds for the patients on whom to evaluation the calculation
	 * @param parameterValues a map of parameter values, takes the form
	 *                        Map&lt;ParameterDefinition.key, Object Value&gt;
	 * @param context         the {@link PatientCalculationContext} to use while performing the evaluation
	 * @return a {@link CalculationResultMap}
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		CalculationResultMap currentART = calculate(new CurrentArtRegimenCalculation(), cohort, context);
		CalculationResultMap ret = new CalculationResultMap();
		for(Integer ptId:cohort){
			Date currentStartDate = null;
			SimpleResult currentArvResult = (SimpleResult) currentART.get(ptId);
			if( currentArvResult != null) {
				RegimenOrder currentRegimen = (RegimenOrder) currentArvResult.getValue();
				Set<DrugOrder> drugs = currentRegimen.getDrugOrders();
				for (DrugOrder o : drugs) {
					if (o.getDateActivated() != null) {
						currentStartDate = o.getDateActivated();
						break;
					}
				}
			}
			ret.put(ptId, new SimpleResult(currentStartDate, this));
		}
		return ret;
	}
}