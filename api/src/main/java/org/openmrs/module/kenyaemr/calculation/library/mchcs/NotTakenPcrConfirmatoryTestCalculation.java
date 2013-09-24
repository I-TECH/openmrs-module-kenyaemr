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

import java.lang.System;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.openmrs.Program;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.api.context.Context;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.calculation.result.ObsResult;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;

/**
 * Determines whether a child has been exited from care and No pcr confirmatory test is done
 *
 */

public class NotTakenPcrConfirmatoryTestCalculation extends BaseEmrCalculation implements PatientFlagCalculation {

	/**
	 * @see org.openmrs.module.kenyacore.calculation.PatientFlagCalculation#getFlagMessage()
	 */
	@Override
	public String getFlagMessage() {
		return "Due For PCR Confirmatory Test";
	}

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Program mchcsProgram = MetadataUtils.getProgram(Metadata.Program.MCHCS);

		Set<Integer> alive = alivePatients(cohort, context);

		Set<Integer> inMchcsProgram = CalculationUtils.patientsThatPass(Calculations.activeEnrollment(mchcsProgram, alive, context));

		//get wheather the child is HIV Exposed
		CalculationResultMap lastChildHivStatus = Calculations.lastObs(getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS), inMchcsProgram, context);

		//check if pcr test was done
		CalculationResultMap lastPcrTest = Calculations.lastObs(getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION), inMchcsProgram, context);

		//check if the context status was set to confirmatory
		CalculationResultMap lastPcrStatus = Calculations.lastObs(getConcept(Dictionary.TEXT_CONTEXT_STATUS), inMchcsProgram, context);

		Concept hivExposed = getConcept(Dictionary.EXPOSURE_TO_HIV);
		Concept pcrCornfirmatory = getConcept(Dictionary.CONFIRMATION_STATUS);

		//get an encounter type for HEI completion
		EncounterType hei_completion_encounterType = MetadataUtils.getEncounterType(Metadata.EncounterType.MCHCS_HEI_COMPLETION);



		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptId : cohort) {

			boolean notTakenConfirmatoryPcrTest = false;
			Patient patient = Context.getPatientService().getPatient(ptId);
			Encounter lastMchcsHeiCompletion = EmrUtils.lastEncounter(patient,hei_completion_encounterType);
			Obs hivStatusObs = EmrCalculationUtils.obsResultForPatient(lastChildHivStatus, ptId);
			Obs pcrObs = EmrCalculationUtils.obsResultForPatient(lastPcrTest, ptId);
			Obs pcrTestConfirmObs =  EmrCalculationUtils.obsResultForPatient(lastPcrStatus, ptId);
			if ( hivStatusObs != null && hivStatusObs.getValueCoded().equals(hivExposed)) {
				if (lastMchcsHeiCompletion != null) {
					if (pcrObs == null){
						notTakenConfirmatoryPcrTest = true;
					}
					// now the pcr is done now check if status is confirmatory status
					else {
						if (pcrTestConfirmObs != null && pcrTestConfirmObs.getValueCoded().equals(pcrCornfirmatory)){
							notTakenConfirmatoryPcrTest = false;
						}
					}
				}
			}
			ret.put(ptId, new BooleanResult(notTakenConfirmatoryPcrTest, this, context));
		}
		return ret;
	}

}