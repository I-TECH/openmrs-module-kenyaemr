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

package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Determines whether a child at 9 months and above has had antibody test
 */
public class NeedsAntibodyTestCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

	/**
	 * @see org.openmrs.module.kenyacore.calculation.PatientFlagCalculation#getFlagMessage()
	 */
	@Override
	public String getFlagMessage() {
		return "Due For Antibody Test";
	}

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program mchcsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHCS);

		// Get all patients who are alive and in MCH-CS program
		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inMchcsProgram = Filters.inProgram(mchcsProgram, alive, context);

		//CalculationResultMap ages = Calculations.ages(cohort, context);

		// Get whether the child is HIV Exposed
		CalculationResultMap lastChildHivStatus = Calculations.lastObs(Dictionary.getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS), inMchcsProgram, context);
		CalculationResultMap lastHivRapidTest1 = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_RAPID_TEST_1_QUALITATIVE), inMchcsProgram, context);
		CalculationResultMap lastHivRapidTest2 = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_RAPID_TEST_2_QUALITATIVE), inMchcsProgram, context);

		Concept hivExposed = Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV);
		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptId : cohort) {
			boolean needsAntibody = false;

			if (inMchcsProgram.contains(ptId)) {
				//Integer ageInMonths = ((Age) ages.get(ptId).getValue()).getFullMonths();
				Obs hivStatusObs = EmrCalculationUtils.obsResultForPatient(lastChildHivStatus, ptId);
				Obs rapidTest1 = EmrCalculationUtils.obsResultForPatient(lastHivRapidTest1, ptId);
				Obs rapidTest2 = EmrCalculationUtils.obsResultForPatient(lastHivRapidTest2, ptId);

				if (hivStatusObs != null
						&& (hivStatusObs.getValueCoded().equals(hivExposed))
						&& rapidTest1 == null
						&& rapidTest2 == null) {
					// only for patients who are nine months and above
					needsAntibody = true; /*(ageInMonths != null && ageInMonths >= 9);*/
				}
			}
			ret.put(ptId, new BooleanResult(needsAntibody, this, context));

		}
		return ret;
	}
}