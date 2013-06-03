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

package org.openmrs.module.kenyaemr.calculation.cd4;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.KenyaEmrConstants;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.calculation.BaseAlertCalculation;
import org.openmrs.module.kenyaemr.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.CalculationUtils;

/**
 * Calculates whether a patient has a declining CD4 count. Calculation returns true if patient
 * is alive, enrolled in the HIV program and last CD4 count is less than CD4 count from 6 months ago
 */
public class DecliningCD4Calculation extends BaseAlertCalculation {

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation#getName()
	 */
	@Override
	public String getName() {
		return "Patients with Declining CD4";
	}

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.BaseAlertCalculation#getAlertMessage()
	 */
	@Override
	public String getAlertMessage() {
		return "Declining CD4";
	}

	@Override
	public String[] getTags() {
		return new String[] { "hiv" };
	}

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program hivProgram = Metadata.getProgram(Metadata.HIV_PROGRAM);

		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> inHivProgram = CalculationUtils.patientsThatPass(activeEnrollment(hivProgram, alive, context));

		// Get the two CD4 obss for comparison
		CalculationResultMap lastCD4Obss = lastObs(getConcept(Dictionary.CD4_COUNT), inHivProgram, context);
		CalculationResultMap oldCD4Obss = lastObsAtLeastDaysAgo(getConcept(Dictionary.CD4_COUNT), KenyaEmrConstants.DECLINING_CD4_COUNT_ACROSS_DAYS, inHivProgram, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			Double lastCD4Count = 0.0;
			Double oldCD4Count = 0.0;
			boolean declining = false;

			// Is patient alive and in HIV program?
			if (inHivProgram.contains(ptId)) {
				lastCD4Count = CalculationUtils.numericObsResultForPatient(lastCD4Obss, ptId);
				oldCD4Count = CalculationUtils.numericObsResultForPatient(oldCD4Obss, ptId);

				if (lastCD4Count != null && oldCD4Count != null) {
					declining = lastCD4Count < oldCD4Count;
				}
			}
			ret.put(ptId, new BooleanResult(declining, this, context));
		}
		return ret;
	}
}
