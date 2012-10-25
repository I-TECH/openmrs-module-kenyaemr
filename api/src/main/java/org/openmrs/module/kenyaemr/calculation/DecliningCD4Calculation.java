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
import java.util.Map;
import java.util.Set;

import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.KenyaEmrConstants;
import org.openmrs.module.kenyaemr.MetadataConstants;

/**
 * Calculates whether a patient has a declining CD4 count. Calculation returns true if patient
 * is alive, enrolled in the HIV program and last CD4 count is less than CD4 count from 6 months ago
 */
public class DecliningCD4Calculation extends KenyaEmrCalculation {

	@Override
	public String getShortMessage() {
		return "Declining CD4";
	}

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program hivProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.HIV_PROGRAM_UUID);

		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> inHivProgram = patientsThatPass(lastProgramEnrollment(hivProgram, alive, context));

		// Get the two CD4 obss for comparison
		CalculationResultMap lastCD4Obss = lastObs(MetadataConstants.CD4_CONCEPT_UUID, inHivProgram, context);
		CalculationResultMap oldCD4Obss = lastObsAtLeastDaysAgo(MetadataConstants.CD4_CONCEPT_UUID, KenyaEmrConstants.DECLINING_CD4_COUNT_ACROSS_DAYS, inHivProgram, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			Double lastCD4Count = 0.0;
			Double oldCD4Count = 0.0;
			boolean declining = false;

			// Is patient alive and in HIV program?
			if (inHivProgram.contains(ptId)) {
				lastCD4Count = numericObsResultForPatient(lastCD4Obss, ptId);
				oldCD4Count = numericObsResultForPatient(oldCD4Obss, ptId);

				if (lastCD4Count != null && oldCD4Count != null) {
					declining = lastCD4Count < oldCD4Count;
				}
			}
			ret.put(ptId, new BooleanResult(declining, this, context));
		}
		return ret;
	}
}
