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

import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculates those patients new in care during the month 4 to 6
 */
public class InCareInMonths4To6During6MonthsReviewPeriodCalculation extends AbstractPatientCalculation {
		/**
		 *  @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
		 * @param cohort
		 * @param params
		 * @param context
		 * @return true for those enrolled in month 4 and 6 patients
		 * @return False for those not enrolled in month 4 and 6 patients
		 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Set<Integer> inHivProgram = Filters.inProgram(hivProgram, cohort, context);
		CalculationResultMap ret = new CalculationResultMap();

		//get the date when review period starts
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(context.getNow());
		calendar.add(Calendar.MONTH, -6);

		//get date 3 months from {startOfReviewPeriod}
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(calendar.getTime());
		calendar1.add(Calendar.MONTH, 3);

		//get a date that is 1 day after the calendar1.getTime()
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(calendar1.getTime());
		calendar2.add(Calendar.DATE, 1);

		CalculationResultMap thoseEnrolledInHiv = Calculations.activeEnrollment(hivProgram, cohort, context);
		Set<Integer> thoseInMonth4To6enrollment = thoseEnrolledInHiv.keySet();

		for(Integer ptId : cohort) {
			boolean isEnrolledIn4to6Month = false;
			if(thoseInMonth4To6enrollment.contains(ptId)) {
				List<PatientProgram> patientProgram = Context.getProgramWorkflowService().getPatientPrograms(Context.getPatientService().getPatient(ptId), hivProgram, calendar2.getTime(), null, null, null, true);
				if (patientProgram.size() > 0) {
					isEnrolledIn4to6Month = true;
				}

			}
			ret.put(ptId, new BooleanResult(isEnrolledIn4to6Month, this, context));
		}
		return ret;
	}
}
