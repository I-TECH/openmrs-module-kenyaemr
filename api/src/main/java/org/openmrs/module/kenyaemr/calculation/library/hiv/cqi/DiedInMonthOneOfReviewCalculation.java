/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv.cqi;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.DeceasedPatientsCalculation;
import org.openmrs.module.kenyaemr.calculation.library.RecordedDeceasedCalculation;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by codehub on 03/12/14.
 */
public class DiedInMonthOneOfReviewCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();

		Set<Integer> deceasedPatients = CalculationUtils.patientsThatPass(calculate(new DeceasedPatientsCalculation(), cohort, context));
		Set<Integer> recordedDeceasedPatients = CalculationUtils.patientsThatPass(calculate(new RecordedDeceasedCalculation(), cohort, context));
		deceasedPatients.addAll(recordedDeceasedPatients);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(context.getNow());
		calendar.add(Calendar.MONTH, -6);

		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(calendar.getTime());
		 calendar1.add(Calendar.MONTH, 1);

		for (Integer ptId : cohort) {
			boolean diedInFirstMonth = false;
			Date deathDate;
			if(deceasedPatients.contains(ptId)) {
				Patient patient = Context.getPatientService().getPatient(ptId);
				if(patient.getDead()) {
					deathDate = patient.getDeathDate();
					if(deathDate.after(calendar.getTime()) && deathDate.before(calendar1.getTime())){
						diedInFirstMonth = true;
					}
				}
			}
			ret.put(ptId, new BooleanResult(diedInFirstMonth, this, context));
		}
		return ret;
	}
}
