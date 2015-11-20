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

import org.openmrs.Encounter;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculates the date a patients was seen last by a provider
 */
public class DateLastSeenCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		CalculationResultMap dateEnrolledMap = Calculations.firstEnrollments(hivProgram, cohort, context);


		if(outcomePeriod != null){
			context.setNow(DateUtil.adjustDate(DateUtil.getStartOfMonth(context.getNow()), outcomePeriod, DurationUnit.MONTHS));
		}

		CalculationResultMap lastEncounter = Calculations.lastEncounter(null, cohort, context);
		CalculationResultMap initialArtStart = calculate(new InitialArtStartDateCalculation(), cohort, context);


		CalculationResultMap result = new CalculationResultMap();
		for (Integer ptId : cohort) {

			Encounter encounter = EmrCalculationUtils.encounterResultForPatient(lastEncounter, ptId);
			PatientProgram patientProgram = EmrCalculationUtils.resultForPatient(dateEnrolledMap, ptId);
			Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(initialArtStart, ptId);
			Date encounterDate = null;
			if(patientProgram != null) {

				if (encounter != null) {
					if(artStartDate != null && artStartDate.after(encounter.getEncounterDatetime())) {
						encounterDate = artStartDate;
					}
					else {
						encounterDate = encounter.getEncounterDatetime();
					}
				}
				if(encounterDate == null && artStartDate != null) {
					encounterDate = artStartDate;
				}

				if(encounterDate == null){
					encounterDate = patientProgram.getDateEnrolled();
				}


			}
			result.put(ptId, new SimpleResult(encounterDate, this));
		}
		return  result;
	}
}
