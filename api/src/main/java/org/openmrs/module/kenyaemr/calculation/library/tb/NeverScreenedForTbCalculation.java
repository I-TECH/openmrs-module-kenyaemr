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

package org.openmrs.module.kenyaemr.calculation.library.tb;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyautil.MetadataUtils;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyacore.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;

/**
 * Calculates HIV patients who have not been screened for TB
 */
public class NeverScreenedForTbCalculation extends BaseEmrCalculation {

	/**
	 * Evaluates the calculation
	 * @param cohort the patient cohort
	 * @param params the calculation parameters
	 * @param context the calculation context
	 * @return the result map
	 * @should calculate null for patients who are not enrolled in the HIV program or not alive
	 * @should calculate true for patients who have no TB screening encounter
	 * @should calculate false for patients who have a TB screening encounter
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
		Program hivProgram = MetadataUtils.getProgram(Metadata.HIV_PROGRAM);
		EncounterType screeningEncType = MetadataUtils.getEncounterType(Metadata.TB_SCREENING_ENCOUNTER_TYPE);

		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> inHivProgram = CalculationUtils.patientsThatPass(activeEnrollment(hivProgram, alive, context));
		Set<Integer> wasScreened = CalculationUtils.patientsThatPass(allEncounters(screeningEncType, cohort, context));

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			BooleanResult result = null;

			if (inHivProgram.contains(ptId)) {
				result = new BooleanResult(!wasScreened.contains(ptId), this);
			}

			ret.put(ptId, result);
		}
		return ret;
	}
}