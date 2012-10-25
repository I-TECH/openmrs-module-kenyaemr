/*
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

package org.openmrs.module.kenyaemr.calculation;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.MetadataConstants;

/**
 * Calculates whether patients have missed their last scheduled return visit. Calculation returns true
 * if the patient is alive, enrolled in the HIV program, has a scheduled return visit in the past,
 * and hasn't had an encounter since that date
 */
public class MissedAppointmentsOrDefaultedCalculation extends KenyaEmrCalculation {

    @Override
    public String getShortMessage() {
        return "Missed Appointments";
    }

    /**
     * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
     * @should calculate false for deceased patients
     * @should calculate false for patients not in HIV program
     * @should calculate false for patients with no return visit date obs
     * @should calculate false for patients with return visit date obs whose value is in the future
     * @should calculate false for patients with encounter after return visit date obs value
     * @should calculate true for patients in HIV program with no encounter after return visit date obs value
     */
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

        Program hivProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.HIV_PROGRAM_UUID);

		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> inHivProgram = patientsThatPass(lastProgramEnrollment(hivProgram, alive, context));
        CalculationResultMap lastReturnDateObss = lastObs(MetadataConstants.RETURN_VISIT_DATE_CONCEPT_UUID, inHivProgram, context);
        CalculationResultMap lastEncounters = lastEncounter(null, cohort, context);

        CalculationResultMap ret = new CalculationResultMap();
        for (Integer ptId : cohort) {
            boolean missedVisit = false;

            // Is patient alive and in the HIV program
            if (inHivProgram.contains(ptId)) {
                Date lastScheduledReturnDate = datetimeObsResultForPatient(lastReturnDateObss, ptId);

                // Does patient have a scheduled return visit in the past
                if (lastScheduledReturnDate != null && daysSince(lastScheduledReturnDate, context) > 0) {

                    // Has patient returned since
                    Encounter lastEncounter = encounterResultForPatient(lastEncounters, ptId);
                    Date lastActualReturnDate = lastEncounter != null ? lastEncounter.getEncounterDatetime() : null;
                    missedVisit = lastActualReturnDate == null || lastActualReturnDate.before(lastScheduledReturnDate);
                }
            }
            ret.put(ptId, new SimpleResult(missedVisit, this, context));
        }
        return ret;
    }
}
