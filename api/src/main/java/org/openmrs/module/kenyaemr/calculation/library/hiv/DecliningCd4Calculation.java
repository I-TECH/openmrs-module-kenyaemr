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

import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether a patient has a declining CD4 count. Calculation returns true if patient
 * is alive, enrolled in the HIV program and last CD4 count is less than CD4 count from 6 months ago
 */
public class DecliningCd4Calculation extends BaseEmrCalculation implements PatientFlagCalculation {

	/**
	 * @see org.openmrs.module.kenyacore.calculation.PatientFlagCalculation#getFlagMessage()
	 */
	@Override
	public String getFlagMessage() {
		return "Declining CD4";
	}

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program hivProgram = MetadataUtils.getProgram(HivMetadata._Program.HIV);

		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> inHivProgram = CalculationUtils.patientsThatPass(Calculations.activeEnrollment(hivProgram, alive, context));

		// Get the two CD4 obss for comparison
		CalculationResultMap lastCD4Obss = Calculations.lastObs(getConcept(Dictionary.CD4_COUNT), inHivProgram, context);
		CalculationResultMap oldCD4Obss = Calculations.lastObsAtLeastDaysAgo(getConcept(Dictionary.CD4_COUNT), EmrConstants.DECLINING_CD4_COUNT_ACROSS_DAYS, inHivProgram, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean declining = false;

			// Is patient alive and in HIV program?
			if (inHivProgram.contains(ptId)) {
				Double lastCD4Count = EmrCalculationUtils.numericObsResultForPatient(lastCD4Obss, ptId);
				Double oldCD4Count = EmrCalculationUtils.numericObsResultForPatient(oldCD4Obss, ptId);

				if (lastCD4Count != null && oldCD4Count != null) {
					declining = lastCD4Count < oldCD4Count;
				}
			}
			ret.put(ptId, new BooleanResult(declining, this, context));
		}
		return ret;
	}
}
