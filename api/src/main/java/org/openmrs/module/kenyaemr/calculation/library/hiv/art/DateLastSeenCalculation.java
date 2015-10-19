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
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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

		CalculationResultMap lastEncounter = Calculations.allEncounters(null, cohort, context);
		CalculationResultMap initialArtStart = calculate(new InitialArtStartDateCalculation(), cohort, context);


		CalculationResultMap result = new CalculationResultMap();
		for (Integer ptId : cohort) {
			ListResult allEncounters = (ListResult) lastEncounter.get(ptId);
			List<Encounter> encounterList = CalculationUtils.extractResultValues(allEncounters);
			PatientProgram patientProgram = EmrCalculationUtils.resultForPatient(dateEnrolledMap, ptId);
			Date artStartDate = EmrCalculationUtils.datetimeResultForPatient(initialArtStart, ptId);
			if(outcomePeriod != null && patientProgram != null) {
				Date futureDate = DateUtil.adjustDate(DateUtil.adjustDate(patientProgram.getDateEnrolled(), outcomePeriod, DurationUnit.MONTHS), 1, DurationUnit.DAYS);
				Date encounterDate = null;
				List<Encounter> targetedEncounters = new ArrayList<Encounter>();
				if (encounterList.size() > 0) {
					for (Encounter encounter : encounterList) {
						if (encounter.getEncounterDatetime().before(futureDate) && (encounter.getEncounterDatetime().after(patientProgram.getDateEnrolled()) || encounter.getEncounterDatetime().equals(patientProgram.getDateEnrolled()))) {
							targetedEncounters.add(encounter);
						}
					}
					if (targetedEncounters.size() > 0) {
						Date encounterDateRequired = targetedEncounters.get(targetedEncounters.size() - 1).getEncounterDatetime();
						if(artStartDate != null && artStartDate.after(encounterDateRequired)) {
							encounterDate = artStartDate;
						}
						else {
							encounterDate = encounterDateRequired;
						}
					}
				}
				if(encounterDate == null && artStartDate != null) {
					encounterDate = artStartDate;
				}

				if(encounterDate == null){
					encounterDate = patientProgram.getDateEnrolled();
				}

				result.put(ptId, new SimpleResult(encounterDate, this));
			}
		}
		return  result;
	}
}
