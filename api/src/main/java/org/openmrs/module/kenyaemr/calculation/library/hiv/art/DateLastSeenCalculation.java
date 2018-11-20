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
