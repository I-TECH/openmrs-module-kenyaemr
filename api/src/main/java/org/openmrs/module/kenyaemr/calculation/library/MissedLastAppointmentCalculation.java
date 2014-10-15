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

package org.openmrs.module.kenyaemr.calculation.library;

import org.openmrs.Encounter;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.LostToFollowUpCalculation;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether patients have missed their last scheduled return visit. Calculation returns true if the patient is
 * alive, has a scheduled return visit in the past, and hasn't had an encounter since that date
 */
public class MissedLastAppointmentCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

	@Override
	public String getFlagMessage() {
		return "Missed Appointment";
	}

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @should calculate false for deceased patients
	 * @should calculate false for patients with no return visit date obs
	 * @should calculate false for patients with return visit date obs whose value is in the future
	 * @should calculate false for patients with encounter after return visit date obs value
	 * @should calculate true for patients with no encounter after return visit date obs value
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Set<Integer> alive = Filters.alive(cohort, context);

		CalculationResultMap lastReturnDateObss = Calculations.lastObs(Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE), alive, context);
		CalculationResultMap lastEncounters = Calculations.lastEncounter(null, cohort, context);
		Set<Integer> ltfu = CalculationUtils.patientsThatPass(calculate(new LostToFollowUpCalculation(), cohort, context));

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean missedVisit = false;

			// Is patient alive
			if (alive.contains(ptId)) {
				Date lastScheduledReturnDate = EmrCalculationUtils.datetimeObsResultForPatient(lastReturnDateObss, ptId);

				// Does patient have a scheduled return visit in the past
				if (lastScheduledReturnDate != null && EmrCalculationUtils.daysSince(lastScheduledReturnDate, context) > 0) {

					// Has patient returned since
					Encounter lastEncounter = EmrCalculationUtils.encounterResultForPatient(lastEncounters, ptId);
					Date lastActualReturnDate = lastEncounter != null ? lastEncounter.getEncounterDatetime() : null;
					missedVisit = lastActualReturnDate == null || lastActualReturnDate.before(lastScheduledReturnDate);
				}

				if (ltfu.contains(ptId)) {
					missedVisit = false;
				}
			}
			ret.put(ptId, new SimpleResult(missedVisit, this, context));
		}
		return ret;
	}
}