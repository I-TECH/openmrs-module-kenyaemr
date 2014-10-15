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

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * calculates the patients who completed initial treatment.
 */
public class TbInitialTreatmentCalculation extends AbstractPatientCalculation {
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
		//only deal with the alive patients
		Set<Integer> alive = Filters.alive(cohort, context);
		//Patients in tb program
		CalculationResultMap inProgram = Calculations.lastEncounter(MetadataUtils.existing(EncounterType.class, TbMetadata._EncounterType.TB_ENROLLMENT), alive, context);
		//find initial observation for completed treatment
		CalculationResultMap treatmentOutcome = Calculations.firstObs(Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME), cohort, context);
		//get the concept of completed treatment
		Concept completedInitialTreatment = Dictionary.getConcept(Dictionary.TREATMENT_COMPLETE);

		CalculationResultMap ret = new CalculationResultMap();
		for(int ptId:cohort){
			boolean completed = false;
			Encounter tbEnrollmentEncounter = EmrCalculationUtils.encounterResultForPatient(inProgram, ptId);
			Concept concept = EmrCalculationUtils.codedObsResultForPatient(treatmentOutcome, ptId);
			if((tbEnrollmentEncounter != null) && (concept != null) && (concept.equals(completedInitialTreatment))){
				completed = true;
			}

			ret.put(ptId, new BooleanResult(completed, this, context));
		}
		return ret;
	}
}
