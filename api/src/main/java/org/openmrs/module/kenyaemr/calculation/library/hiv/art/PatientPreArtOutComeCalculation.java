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

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.AliveAndOnFollowUpCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.DateClassifiedLTFUCalculation;
import org.openmrs.module.kenyaemr.calculation.library.models.LostToFU;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfDeathCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculate possible patient outcomes at the end of the cohort period
 */
public class PatientPreArtOutComeCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Integer months = (parameterValues != null && parameterValues.containsKey("months")) ? (Integer) parameterValues.get("months") : null;

		PatientCalculationService patientCalculationService = Context.getService(PatientCalculationService.class);

		PatientCalculationContext context1 = patientCalculationService.createCalculationContext();
		if(months == null) {
			months = 0;
		}
		CalculationResultMap enrolledHere = calculate( new DateOfEnrollmentCalculation(),cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
		   String status = null;
			Date dateLost = null;

			Date patientProgramDate = EmrCalculationUtils.resultForPatient(enrolledHere, ptId);

			if(patientProgramDate != null && monthsSince(patientProgramDate, new Date()) >= months ) {

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(patientProgramDate);
				calendar.add(Calendar.MONTH, months);
				context1.setNow(calendar.getTime());


				CalculationResultMap onART = calculate(new InitialArtStartDateCalculation(), cohort, context1);
				//find the initial art start date
				Date initialArtStart = EmrCalculationUtils.datetimeResultForPatient(onART, ptId);

				CalculationResultMap deadPatients = calculate(new DateOfDeathCalculation(), cohort, context1);
				Date dod = EmrCalculationUtils.datetimeResultForPatient(deadPatients, ptId);

				CalculationResultMap transferredOut = calculate(new TransferOutDateCalculation(), cohort, context1);
				//find date transferred out
				Date dateTo = EmrCalculationUtils.datetimeResultForPatient(transferredOut, ptId);

				CalculationResultMap defaulted = calculate(new DateDefaultedCalculation(), cohort, context1);
				//find date defaulted
				Date defaultedDate = EmrCalculationUtils.datetimeResultForPatient(defaulted, ptId);

				CalculationResultMap ltfu = calculate(new DateClassifiedLTFUCalculation(), cohort, context1);
				LostToFU classifiedLTFU = EmrCalculationUtils.resultForPatient(ltfu, ptId);
				if(classifiedLTFU != null) {
					dateLost = (Date) classifiedLTFU.getDateLost();
				}


				status = "Alive and not on ART";

				if(initialArtStart != null && (initialArtStart.before(calendar.getTime()) || initialArtStart.equals(calendar.getTime())) && initialArtStart.after(patientProgramDate)) {
					status = "Initiated ART";
				}

				if(dod != null && (dod.before(calendar.getTime()) || dod.equals(calendar.getTime()))) {
					status = "Died";
				}

				if(dateTo != null && (dateTo.before(calendar.getTime()) || dateTo.equals(calendar.getTime()))) {
					status = "Transferred out";
				}

				if(dateLost != null && (dateLost.before(calendar.getTime()) || dateLost.equals(calendar.getTime()))){
					status = "Lost to follow up";
				}

				if(defaultedDate != null && (defaultedDate.before(calendar.getTime()) || defaultedDate.equals(calendar.getTime()))){
					status = "Defaulted";
				}

			}
			ret.put(ptId, new SimpleResult(status, this));
		}
		 return  ret;
	}

	private  int monthsSince(Date date1, Date date2) {
		DateTime d1 = new DateTime(date1.getTime());
		DateTime d2 = new DateTime(date2.getTime());
		return Months.monthsBetween(d1, d2).getMonths();
	}
}
