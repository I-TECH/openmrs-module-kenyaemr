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
import org.joda.time.Days;
import org.joda.time.Months;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
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
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.DeceasedPatientsCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.AliveAndOnFollowUpCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.DateClassifiedLTFUCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LostToFollowUpCalculation;
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

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		CalculationResultMap enrolledHere = Calculations.activeEnrollment(hivProgram, cohort, context);

		//bring in all the required outcome maps
		CalculationResultMap deadPatients = calculate(new DateOfDeathCalculation(), cohort, context);
		CalculationResultMap defaulted = calculate(new DateDefaultedCalculation(), cohort, context);
		CalculationResultMap ltfu = calculate(new DateClassifiedLTFUCalculation(), cohort, context);
		CalculationResultMap transferredOut = calculate(new TransferOutDateCalculation(), cohort, context);
		CalculationResultMap onART = calculate(new InitialArtStartDateCalculation(), cohort, context);
		CalculationResultMap aliveAndOnFollowUp = calculate(new AliveAndOnFollowUpCalculation(), cohort, context);


		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
		   String status = null;
			Date dateLost = null;
			PatientProgram patientProgram = EmrCalculationUtils.resultForPatient(enrolledHere, ptId);

			//evaluate the calculation result maps

			Date dod = EmrCalculationUtils.datetimeResultForPatient(deadPatients, ptId);

			LostToFU classifiedLTFU = EmrCalculationUtils.resultForPatient(ltfu, ptId);

			//find date transferred out
			Date dateTo = EmrCalculationUtils.datetimeResultForPatient(transferredOut, ptId);

			//find the initial art start date
			Date initialArtStart = EmrCalculationUtils.datetimeResultForPatient(onART, ptId);

			//find date defaulted
			Date defaultedDate = EmrCalculationUtils.datetimeResultForPatient(defaulted, ptId);

			if(classifiedLTFU != null) {
				dateLost = (Date) classifiedLTFU.getDateLost();
			}

			if(patientProgram != null && months != null && (monthsSince(patientProgram.getDateEnrolled(), new Date()) >= months)) {

				status = "A";
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
