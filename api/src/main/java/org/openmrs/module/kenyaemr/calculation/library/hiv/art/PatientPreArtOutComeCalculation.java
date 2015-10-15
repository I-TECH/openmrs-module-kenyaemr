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

import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.DateClassifiedLTFUCalculation;
import org.openmrs.module.kenyaemr.calculation.library.models.LostToFU;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfDeathCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Calculate possible patient outcomes at the end of the cohort period
 */
public class PatientPreArtOutComeCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		CalculationResultMap enrolledHere = Calculations.firstEnrollments(hivProgram, cohort, context);

		if(outcomePeriod != null){
			context.setNow(DateUtil.adjustDate(context.getNow(), outcomePeriod, DurationUnit.MONTHS));
		}


		CalculationResultMap onART = calculate(new InitialArtStartDateCalculation(), cohort, context);
		CalculationResultMap deadPatients = calculate(new DateOfDeathCalculation(), cohort, context);
		CalculationResultMap transferredOut = calculate(new TransferOutDateCalculation(), cohort, context);
		CalculationResultMap defaulted = calculate(new DateDefaultedCalculation(), cohort, context);
		CalculationResultMap ltfu = calculate(new DateClassifiedLTFUCalculation(), cohort, context);

		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptId : cohort) {
		   	String status = "Alive and not on ART";
			Date dateLost = null;
			TreeMap<Date, String> preArtOutcomes = new TreeMap<Date, String>();

			PatientProgram patientProgramDate = EmrCalculationUtils.resultForPatient(enrolledHere, ptId);

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
				Date futureDate = DateUtil.adjustDate(DateUtil.adjustDate(patientProgramDate.getDateEnrolled(), outcomePeriod, DurationUnit.MONTHS), 1, DurationUnit.DAYS);


				//start looping through to get outcomes
				if(initialArtStart != null && initialArtStart.before(futureDate) && initialArtStart.after(patientProgramDate.getDateEnrolled())) {
					preArtOutcomes.put(initialArtStart, "Initiated ART");
				}

				if(dod != null && dateTo != null && dod.before(dateTo) && dod.before(futureDate) && dod.after(patientProgramDate.getDateEnrolled())) {
					preArtOutcomes.put(dod, "Died");
				}
				if(dod != null && dateTo != null && dateTo.before(dod) && dateTo.before(futureDate) && dateTo.after(patientProgramDate.getDateEnrolled())) {
					preArtOutcomes.put(dateTo, "Transferred out");
				}

				if(dod != null && dod.before(futureDate) && dod.after(patientProgramDate.getDateEnrolled())){
					preArtOutcomes.put(dod, "Died");
				}
				if(dateTo != null && dateTo.before(futureDate) && dateTo.after(patientProgramDate.getDateEnrolled())){
					preArtOutcomes.put(dateTo, "Transferred out");
				}

				if(defaultedDate != null && dateLost != null && defaultedDate.before(dateLost) && defaultedDate.before(futureDate) && defaultedDate.after(patientProgramDate.getDateEnrolled())){
					preArtOutcomes.put(defaultedDate, "Defaulted");
				}

				if(defaultedDate != null && dateLost != null && dateLost.before(defaultedDate) && dateLost.before(futureDate) && dateLost.after(patientProgramDate.getDateEnrolled()) && dateLost.before(new Date())){
					preArtOutcomes.put(dateLost, "LTFU");
				}
				if(defaultedDate != null && defaultedDate.before(futureDate) && defaultedDate.after(patientProgramDate.getDateEnrolled()) && defaultedDate.before(new Date())) {
					preArtOutcomes.put(defaultedDate, "Defaulted");
				}

				if(dateLost != null && dateLost.before(futureDate) && dateLost.after(patientProgramDate.getDateEnrolled()) && dateLost.before(new Date())) {
					preArtOutcomes.put(dateLost, "LTFU");
				}
				//pick the last item in the tree map
				//check first if it is null
				if(preArtOutcomes.size() > 0) {
					Map.Entry<Date, String> values = preArtOutcomes.lastEntry();
					if(values != null){
						status = values.getValue();
					}
				}
				ret.put(ptId, new SimpleResult(status, this));
			}

		}
		 return  ret;
	}

}
