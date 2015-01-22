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

import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.reporting.common.Age;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by codehub on 22/01/15.
 */
public class AgeAtARTInitiationCalculation extends AbstractPatientCalculation {
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

		CalculationResultMap artStartDateMap = calculate(new InitialArtStartDateCalculation(), cohort, context);
		CalculationResultMap ret = new CalculationResultMap();
		for(Integer ptId:cohort){
			Integer ageAtARTStart = null;
			Date birthDate = Context.getPatientService().getPatient(ptId).getBirthdate();
			Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDateMap, ptId);
			if (artStartDate != null && birthDate != null){
				ageAtARTStart = ageInYearsAtDate(birthDate, artStartDate);
			}
			ret.put(ptId, new SimpleResult(ageAtARTStart, this, context));
		}
		return ret;
	}

	private Integer ageInYearsAtDate(Date birthDate, Date artInitiationDate) {
		Age age = new Age(birthDate, artInitiationDate);
		return age.getFullYears();
	}
}
