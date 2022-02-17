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

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.reporting.common.Age;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculates  patients are have been on ART for more than 3 months (>3)
 */
public class MoreThanThreeMonthsOnARTCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		CalculationResultMap artStartDateMap = calculate(new InitialArtStartDateCalculation(), cohort, context);
		CalculationResultMap ret = new CalculationResultMap();
		for(Integer ptId:cohort){
			Boolean eligible = false;
			Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(artStartDateMap, ptId);
			if (artStartDate != null && (monthsBetween(artStartDate, context.getNow()) >= 3)){
				eligible = true;
			}
			ret.put(ptId, new BooleanResult(eligible, this));
		}
		return ret;
	}

	int monthsBetween(Date d1, Date d2) {
		DateTime dateTime1 = new DateTime(d1.getTime());
		DateTime dateTime2 = new DateTime(d2.getTime());
		return Math.abs(Months.monthsBetween(dateTime1, dateTime2).getMonths());
	}
}
