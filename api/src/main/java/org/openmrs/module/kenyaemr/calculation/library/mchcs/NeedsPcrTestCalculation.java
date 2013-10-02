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
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Determines whether a child at 6 week and above has had PCR test
 *
*/

public class NeedsPcrTestCalculation extends BaseEmrCalculation implements PatientFlagCalculation {

	/**
	 * @see org.openmrs.module.kenyacore.calculation.PatientFlagCalculation#getFlagMessage()
	 */
	@Override
	public String getFlagMessage() {
		return "Due For PCR Test";
	}

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program mchcsProgram = MetadataUtils.getProgram(MchMetadata._Program.MCHCS);

		Set<Integer> alive = alivePatients(cohort, context);

		Set<Integer> inMchcsProgram = CalculationUtils.patientsThatPass(Calculations.activeEnrollment(mchcsProgram, alive, context));

		//get wheather the child is HIV Exposed
		CalculationResultMap lastChildHivStatus = Calculations.lastObs(getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS), inMchcsProgram, context);
		CalculationResultMap lastPcrTest = Calculations.lastObs(getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION), inMchcsProgram, context);

		Concept hivExposed = getConcept(Dictionary.EXPOSURE_TO_HIV);

		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptId : cohort) {

			boolean needsPcr = false;

			//check if a patient is alive and is in mchcs program
			if (inMchcsProgram.contains(ptId)) {

				Obs hivStatusObs = EmrCalculationUtils.obsResultForPatient(lastChildHivStatus, ptId);
				Obs pcrTestObs =  EmrCalculationUtils.obsResultForPatient(lastPcrTest, ptId);

				if (hivStatusObs != null && pcrTestObs == null && (hivStatusObs.getValueCoded().equals(hivExposed))) {

					needsPcr = true;
				}

				ret.put(ptId, new BooleanResult(needsPcr, this, context));
			}
		}
		return ret;
	}
}