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

package org.openmrs.module.kenyaemr.calculation;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.MetadataConstants;

import java.util.*;

/**
 * Calculates the last recorded WHO stage of patients. Calculation returns NULL for patients with no recorded WHO stage
 */
public class WHOStagesAtEnrollmentsCalculation extends BaseEmrCalculation {

    @Override
    public String getName() {
        return "WHO Stages at Enrollments";
    }

	@Override
	public String[] getTags() {
		return new String[] { "hiv" };
	}

    /**
     * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
     */
    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program hivProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.HIV_PROGRAM_UUID);

		// Get each patients HIV Program enrollments
		CalculationResultMap hivEnrollments = allProgramEnrollments(hivProgram, cohort, context);

		// Get each patients WHO stage obs (these will be ordered by date ascending)
		CalculationResultMap whoStageObss = allObs(getConcept(MetadataConstants.WHO_STAGE_CONCEPT_UUID), cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			ListResult patientHivEnrollments = (ListResult) hivEnrollments.get(ptId);
			ListResult patientWhoStageObss = (ListResult) whoStageObss.get(ptId);

			Map<PatientProgram, Concept> whoStageAtEnrollment = new HashMap<PatientProgram, Concept>();

			if (patientHivEnrollments != null && patientWhoStageObss != null) {

				List<PatientProgram> patientPrograms = CalculationUtils.extractListResultValues(patientHivEnrollments);
				for (PatientProgram patientProgram : CalculationUtils.<PatientProgram>extractListResultValues(patientHivEnrollments)) {

					Date enrollmentDate = patientProgram.getDateEnrolled();
					Date completedDate = patientProgram.getDateCompleted();

					// Get the first WHO Stage obs on or after the enrollment date but before the discontinue date
					for (Obs obs : CalculationUtils.<Obs>extractListResultValues(patientWhoStageObss)) {
						if (obs.getObsDatetime().compareTo(enrollmentDate) >= 0 && (completedDate == null || obs.getObsDatetime().compareTo(completedDate) < 0)) {
							whoStageAtEnrollment.put(patientProgram, obs.getValueCoded());
							break;
						}
					}
				}
			}

			ret.put(ptId, new SimpleResult(whoStageAtEnrollment, this));
		}

		return ret;
    }
}