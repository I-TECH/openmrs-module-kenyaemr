/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.HivConstants;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils.daysSince;

/**
 * Calculates whether a patient has been lost to follow up. Calculation returns true if patient
 * is alive, enrolled in the HIV program, but hasn't had an encounter in LOST_TO_FOLLOW_UP_THRESHOLD_DAYS days
 */
public class LostToFollowUpCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

	@Override
	public String getFlagMessage() {
		return "Lost to Followup";
	}

	/**
	 * Evaluates the calculation
	 * @should calculate false for deceased patients
	 * @should calculate false for patients not in HIV program
	 * @should calculate false for patients with an encounter in last LOST_TO_FOLLOW_UP_THRESHOLD_DAYS days days since appointment date
	 * @should calculate true for patient with no encounter in last LOST_TO_FOLLOW_UP_THRESHOLD_DAYS days days since appointment date
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> arg1, PatientCalculationContext context) {

		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Concept reasonForDiscontinuation = Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION);
		Concept transferout = Dictionary.getConcept(Dictionary.TRANSFERRED_OUT);

		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);

		//CalculationResultMap lastEncounters = Calculations.lastEncounter(null, inHivProgram, context);
		CalculationResultMap lastReturnDateObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE), inHivProgram, context);
		CalculationResultMap lastProgramDiscontinuation = Calculations.lastObs(reasonForDiscontinuation, cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean lost = false;

			// Is patient alive and in the HIV program
			if (alive.contains(ptId)) {

				// Patient is lost if no encounters in last X days
				//Encounter lastEncounter = EmrCalculationUtils.encounterResultForPatient(lastEncounters, ptId);
				Date lastScheduledReturnDate = EmrCalculationUtils.datetimeObsResultForPatient(lastReturnDateObss, ptId);
				Obs discontuation = EmrCalculationUtils.obsResultForPatient(lastProgramDiscontinuation, ptId);
				if (lastScheduledReturnDate != null) {
					if(daysSince(lastScheduledReturnDate, context) > HivConstants.LOST_TO_FOLLOW_UP_THRESHOLD_DAYS){
						lost = true;
					}
					if(discontuation != null && discontuation.getValueCoded().equals(transferout)) {
						lost = false;
					}
				}

			}
			ret.put(ptId, new SimpleResult(lost, this, context));

		}
		return ret;
	}
}
