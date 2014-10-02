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

package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import org.openmrs.Obs;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Calculation for infants who received DNA PCR test at the age of 6(n) weeks
 */
public class InfantsDNAPCRCalculation extends AbstractPatientCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {


		// Get whether  the child has dna pcr test obs
		CalculationResultMap infantsWithDNAPCR = Calculations.allObs(Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION),cohort, context);
		CalculationResultMap ret = new CalculationResultMap();

		//get param val for age
		Integer durationAfterBirth = (Integer)parameterValues.get("durationAfterBirth");
		PatientService service = Context.getPatientService();
		for (Integer ptId : cohort) {
			boolean receivedTest = false;

			ListResult result = (ListResult) infantsWithDNAPCR.get(ptId);
			List<Obs> obs = CalculationUtils.extractResultValues(result);
			Date dob = service.getPatient(ptId).getBirthdate();

			if (dob != null && !obs.isEmpty()) {
				/**
				 * We convert durationAfterBirth to days to allow for a given acceptable range
				 * no_of_days = durationAfterBirth X 7
				 * TODO: Confirm the right no of days before and after a given date considered within x weeks after birth
				 * for example: 6 weeks = 6 X 7 = 42
				 * lower boundary can be 40 with upper boundary as 46
				 */
				Calendar target_date = setCalendarTime(dob);
				target_date.add(Calendar.DAY_OF_YEAR, (durationAfterBirth * 7));

				Calendar lower_boundary = setCalendarTime(target_date.getTime());
				lower_boundary.add(Calendar.DAY_OF_YEAR, -2);

				Calendar upper_boundary = setCalendarTime(target_date.getTime());
				upper_boundary.add(Calendar.DAY_OF_YEAR, 6);


				for (Obs o : obs) {
					if(o != null) {

						Calendar obs_date = setCalendarTime(o.getObsDatetime());
						if (obs_date.after(lower_boundary) && obs_date.before(upper_boundary)) {
							receivedTest = true;
							break;
						}
					}
				}

				ret.put(ptId, new BooleanResult(receivedTest, this));
			}
		}
		return ret;
	}

	private Calendar setCalendarTime(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.clear(Calendar.HOUR);
		cal.clear(Calendar.HOUR_OF_DAY);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		return cal;

	}

}