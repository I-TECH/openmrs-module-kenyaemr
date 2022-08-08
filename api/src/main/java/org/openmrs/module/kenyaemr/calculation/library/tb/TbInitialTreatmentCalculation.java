/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
