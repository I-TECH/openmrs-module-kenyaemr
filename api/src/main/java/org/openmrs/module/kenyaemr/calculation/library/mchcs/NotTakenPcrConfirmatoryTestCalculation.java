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
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyacore.calculation.PatientFlagCalculation;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Determines whether a child has been exited from care and No pcr confirmatory test is done
 */
public class NotTakenPcrConfirmatoryTestCalculation extends AbstractPatientCalculation implements PatientFlagCalculation {

	/**
	 * @see org.openmrs.module.kenyacore.calculation.PatientFlagCalculation#getFlagMessage()
	 */
	@Override
	public String getFlagMessage() {
		return "Due For PCR Confirmatory Test";
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

		// Get whether the child is HIV Exposed
		CalculationResultMap lastChildHivStatus = Calculations.lastObs(Dictionary.getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS), cohort, context);

		//check if pcr test was done
		CalculationResultMap lastPcrTest = Calculations.lastObs(Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION_QUALITATIVE), cohort, context);

		//check if the context status was set to confirmatory
		CalculationResultMap lastPcrStatus = Calculations.lastObs(Dictionary.getConcept(Dictionary.TEXT_CONTEXT_STATUS), cohort, context);

		Concept hivExposed = Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV);
		Concept pcrCornfirmatory = Dictionary.getConcept(Dictionary.CONFIRMATION_STATUS);

		//get an encounter type for HEI completion
		EncounterType hei_completion_encounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHCS_HEI_COMPLETION);
		//load all patient last encounters of HEI completion encounter type
		CalculationResultMap lastEncounters = Calculations.lastEncounter(hei_completion_encounterType,cohort,context);


		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptId : cohort) {
			boolean notTakenConfirmatoryPcrTest = false;

			Encounter lastMchcsHeiCompletion = EmrCalculationUtils.encounterResultForPatient(lastEncounters, ptId);

			Obs hivStatusObs = EmrCalculationUtils.obsResultForPatient(lastChildHivStatus, ptId);
			Obs pcrObs = EmrCalculationUtils.obsResultForPatient(lastPcrTest, ptId);
			Obs pcrTestConfirmObs =  EmrCalculationUtils.obsResultForPatient(lastPcrStatus, ptId);

			if ( inMchcsProgram.contains(ptId) && hivStatusObs != null && hivStatusObs.getValueCoded().equals(hivExposed)) {
				if (lastMchcsHeiCompletion != null) {
					if (pcrObs == null || pcrTestConfirmObs == null || pcrTestConfirmObs.getValueCoded() != pcrCornfirmatory){
						notTakenConfirmatoryPcrTest = true;
					}
				}
			}

			ret.put(ptId, new BooleanResult(notTakenConfirmatoryPcrTest, this, context));
		}
		return ret;
	}
}