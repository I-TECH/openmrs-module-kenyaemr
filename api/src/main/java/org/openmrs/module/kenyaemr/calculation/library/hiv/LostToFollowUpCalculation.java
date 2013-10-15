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

package org.openmrs.module.kenyaemr.calculation.library.hiv;


import org.openmrs.Encounter;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether a patient has been lost to follow up. Calculation returns true if patient
 * is alive, enrolled in the HIV program, but hasn't had an encounter in LOST_TO_FOLLOW_UP_THRESHOLD_DAYS days
 */
public class LostToFollowUpCalculation extends BaseEmrCalculation implements PatientFlagCalculation {

	@Override
	public String getFlagMessage() {
		return "Lost to Followup";
	}

	/**
	 * Evaluates the calculation
	 * @should calculate false for deceased patients
	 * @should calculate false for patients not in HIV program
	 * @should calculate false for patients with an encounter in last LOST_TO_FOLLOW_UP_THRESHOLD_DAYS days
	 * @should calculate true for patient with no encounter in last LOST_TO_FOLLOW_UP_THRESHOLD_DAYS days
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> arg1, PatientCalculationContext context) {

		Program hivProgram = MetadataUtils.getProgram(HivMetadata._Program.HIV);

		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> inHivProgram = CalculationUtils.patientsThatPass(Calculations.activeEnrollment(hivProgram, alive, context));
		CalculationResultMap lastEncounters = Calculations.lastEncounter(null, inHivProgram, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean lost = false;

			// Is patient alive and in the HIV program
			if (inHivProgram.contains(ptId)) {

				// Patient is lost if no encounters in last X days
				Encounter lastEncounter = EmrCalculationUtils.encounterResultForPatient(lastEncounters, ptId);
				Date lastEncounterDate = lastEncounter != null ? lastEncounter.getEncounterDatetime() : null;
				lost = lastEncounterDate == null || daysSince(lastEncounterDate, context) > EmrConstants.LOST_TO_FOLLOW_UP_THRESHOLD_DAYS;
			}
			ret.put(ptId, new SimpleResult(lost, this, context));

		}
		return ret;
	}
}
