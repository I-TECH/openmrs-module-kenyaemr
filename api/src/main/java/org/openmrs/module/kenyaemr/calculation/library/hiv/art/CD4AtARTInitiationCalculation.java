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
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by codehub on 22/01/15.
 */
public class CD4AtARTInitiationCalculation  extends AbstractPatientCalculation {
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

		CalculationResultMap ret = new CalculationResultMap();
		CalculationResultMap artStartDateMap = calculate(new InitialArtStartDateCalculation(), cohort, context);
		CalculationResultMap cd4Counts = Calculations.allObs(Dictionary.getConcept(Dictionary.CD4_COUNT), cohort, context);
		for(Integer ptId: cohort) {
			Double cd4Results = null;
			Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDateMap, ptId);
			ListResult cd4CountResults = (ListResult) cd4Counts.get(ptId);
			;
			List<Obs> obsListCd4Count = new ArrayList<Obs>();

			if(cd4CountResults != null) {
				obsListCd4Count = CalculationUtils.extractResultValues(cd4CountResults);
			}

			if(artStartDate != null) {
				Date aDayBefore = CoreUtils.dateAddDays(artStartDate, -2);
				Date aDayAfter = CoreUtils.dateAddDays(artStartDate, 2);
				for(Obs cd4CountObs:obsListCd4Count){
					if(cd4CountObs.getObsDatetime().after(aDayBefore) && cd4CountObs.getObsDatetime().before(aDayAfter)) {
						cd4Results = cd4CountObs.getValueNumeric();
					}
				}
			}
			ret.put(ptId, new SimpleResult(cd4Results, this));
		}
		return ret;
	}
}
