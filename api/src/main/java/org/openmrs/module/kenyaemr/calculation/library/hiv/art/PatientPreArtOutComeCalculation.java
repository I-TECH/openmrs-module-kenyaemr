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
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.DateClassifiedLTFUCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.DateOfEnrollmentHivCalculation;
import org.openmrs.module.kenyaemr.calculation.library.models.LostToFU;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfDeathCalculation;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculate possible patient outcomes at the end of the cohort period
 */
public class PatientPreArtOutComeCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;
		Date futureDate;
		PatientCalculationService service = Context.getService(PatientCalculationService.class);
		PatientCalculationContext newContext = service.createCalculationContext();
		if(outcomePeriod == null){
			newContext = context;
			futureDate = context.getNow();
		}
		else {
			newContext.setNow(DateUtil.adjustDate(context.getNow(), outcomePeriod, DurationUnit.MONTHS));
			//futureDate = DateUtil.adjustDate(DateUtil.getStartOfMonth(context.getNow()), outcomePeriod, DurationUnit.MONTHS);
		}

		CalculationResultMap enrolledHere = calculate( new DateOfEnrollmentHivCalculation(),cohort, context);
		CalculationResultMap onART = calculate(new InitialArtStartDateCalculation(), cohort, newContext);
		CalculationResultMap deadPatients = calculate(new DateOfDeathCalculation(), cohort, newContext);
		CalculationResultMap transferredOut = calculate(new TransferOutDateCalculation(), cohort, newContext);
		CalculationResultMap defaulted = calculate(new DateDefaultedCalculation(), cohort, newContext);
		CalculationResultMap ltfu = calculate(new DateClassifiedLTFUCalculation(), cohort, newContext);

		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptId : cohort) {
		   	String status = "Alive and not on ART";
			Date dateLost = null;

			Date patientProgramDate = EmrCalculationUtils.resultForPatient(enrolledHere, ptId);

			if(patientProgramDate != null && outcomePeriod != null) {
				Date initialArtStart = EmrCalculationUtils.datetimeResultForPatient(onART, ptId);
				Date dod = EmrCalculationUtils.datetimeResultForPatient(deadPatients, ptId);
				Date dateTo = EmrCalculationUtils.datetimeResultForPatient(transferredOut, ptId);
				Date defaultedDate = EmrCalculationUtils.datetimeResultForPatient(defaulted, ptId);
				LostToFU classifiedLTFU = EmrCalculationUtils.resultForPatient(ltfu, ptId);
				if(classifiedLTFU != null) {
					dateLost = (Date) classifiedLTFU.getDateLost();
				}
				//get future date that would be used as a limit
				futureDate = DateUtil.adjustDate(DateUtil.adjustDate(patientProgramDate, outcomePeriod, DurationUnit.MONTHS), 1, DurationUnit.DAYS);


				//start looping through to get outcomes
				if(initialArtStart != null && initialArtStart.before(futureDate) ) {
					status = "Initiated ART";
				}
				else {
					if(dod != null && dateTo != null && dod.before(dateTo) && dod.before(futureDate)) {
						status = "Died";
					}
					else if(dod != null && dateTo != null && dateTo.before(dod) && dateTo.before(futureDate)) {
						status = "Transferred out";
					}

					else if(dod != null && dod.before(futureDate)){
						status = "Died";
					}
					else if(dateTo != null && dateTo.before(futureDate)){
						status = "Transferred out";
					}

					else if(defaultedDate != null && dateLost != null && defaultedDate.before(dateLost) && defaultedDate.before(futureDate)){
						status = "Defaulted";
					}

					else if(defaultedDate != null && dateLost != null && dateLost.before(defaultedDate) && dateLost.before(futureDate)){
						status = "LTFU";
					}
					else if(defaultedDate != null && defaultedDate.before(futureDate)) {
						status = "Defaulted";
					}

					else if(dateLost != null && dateLost.before(futureDate)) {
						status = "LTFU";
					}
				}
				ret.put(ptId, new SimpleResult(status, this));
			}

		}
		 return  ret;
	}

}
