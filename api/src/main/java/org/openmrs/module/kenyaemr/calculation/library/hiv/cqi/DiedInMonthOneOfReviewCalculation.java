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
