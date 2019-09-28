/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculation for patients who attain a given age within a specified period
 */
public class HEICohortsXMonthsDuringReviewCalculation extends AbstractPatientCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();


		Integer turnedMonths = (Integer)parameterValues.get("turnedMonths"); // age (in months) of interest
		Integer reviewMonths = (Integer)parameterValues.get("reviewMonths"); // no of months ago within which search should consider
		PatientService service = Context.getPatientService();

		Calendar upper_boundary = setCalendarTime(context.getNow());

		Calendar lower_boundary = setCalendarTime(context.getNow());
		lower_boundary.add(Calendar.MONTH, -(reviewMonths));

		for (Integer ptId : cohort) {
			boolean eligibility = false;
			Date dob = service.getPatient(ptId).getBirthdate();

			if (dob != null) {

				Calendar nthBirthDay = getDateOfNthBirthday(dob, turnedMonths);

				if ((nthBirthDay.equals(lower_boundary) || nthBirthDay.after(lower_boundary)) && (nthBirthDay.equals(upper_boundary) || nthBirthDay.before(upper_boundary))) {
					eligibility = true;
				}

				ret.put(ptId, new BooleanResult(eligibility, this));
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

	protected Calendar getDateOfNthBirthday(Date dob, int ageInMonths){
		Calendar dobCal = setCalendarTime(dob);
		dobCal.add(Calendar.MONTH, ageInMonths);
		return dobCal;
	}

}