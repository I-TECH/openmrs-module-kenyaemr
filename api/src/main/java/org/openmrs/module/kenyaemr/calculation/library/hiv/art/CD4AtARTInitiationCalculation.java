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

import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

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
			SimpleResult result = null;
			Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDateMap, ptId);
			ListResult cd4CountResults = (ListResult) cd4Counts.get(ptId);
			List<Obs> obsListCd4Count;

			if(cd4CountResults != null && artStartDate != null) {
				obsListCd4Count = CalculationUtils.extractResultValues(cd4CountResults);
				Obs lastBeforeArtStart = EmrCalculationUtils.findLastOnOrBefore(obsListCd4Count, artStartDate);

				if (lastBeforeArtStart != null) {
					Double cd4 = lastBeforeArtStart.getValueNumeric();
					if(cd4 != null){
						result = new SimpleResult(cd4, this);
					}
				}
			}

			ret.put(ptId, result);
		}
		return ret;
	}
}