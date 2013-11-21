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

package org.openmrs.module.kenyaemr.calculation.library.mchms;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.ArtAssessmentMethod;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Calculates whether a patient was screened for ART eligibility by the given {@link org.openmrs.module.kenyaemr.ArtAssessmentMethod}
 * during her first ANC visit. If no {@link org.openmrs.module.kenyaemr.ArtAssessmentMethod} is specified, any of the two methods
 * is assumed.
 *
 * @params A map of parameters values specifying the {@link org.openmrs.module.kenyaemr.ArtAssessmentMethod}
 * @return
 */
public class AssessedOnFirstVisitCalculation extends BaseEmrCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		ArtAssessmentMethod artAssessmentMethod = (params != null && params.containsKey("artAssessmentMethod")) ?
				(ArtAssessmentMethod) params.get("artAssessmentMethod") : null;

		Program mchmsProgram = MetadataUtils.getProgram(MchMetadata._Program.MCHMS);
		EncounterType mchConsultation = MetadataUtils.getEncounterType(MchMetadata._EncounterType.MCHMS_CONSULTATION);
		Concept whoStageConcept = getConcept(Dictionary.CURRENT_WHO_STAGE);
		Concept cd4CountConcept = getConcept(Dictionary.CD4_COUNT);

		Set<Integer> alivePatients = alivePatients(cohort, context);
		CalculationResultMap activePatientPrograms = Calculations.activeEnrollment(mchmsProgram, alivePatients, context);

		Set<Integer> aliveMchmsPatients = CalculationUtils.patientsThatPass(activePatientPrograms);

		CalculationResultMap crm = Calculations.firstEncounter(mchConsultation, aliveMchmsPatients, context);

		CalculationResultMap resultMap = new CalculationResultMap();

		for (Integer ptId : cohort) {
			boolean qualified = false;
			Encounter encounter = EmrCalculationUtils.encounterResultForPatient(crm, ptId);
			if (encounter != null) {
				if (artAssessmentMethod == ArtAssessmentMethod.WHO_STAGING) {
					Obs whoStageObs = EmrUtils.firstObsInEncounter(encounter, whoStageConcept);
					qualified = whoStageObs != null && whoStageObs.getValueCoded() != null;
				} else if (artAssessmentMethod == ArtAssessmentMethod.CD4_COUNT) {
					Obs cd4CountObs = EmrUtils.firstObsInEncounter(encounter, cd4CountConcept);
					qualified = cd4CountObs != null && cd4CountObs.getValueNumeric() != null;
				} else {
					Obs whoStageObs = EmrUtils.firstObsInEncounter(encounter, whoStageConcept);
					Obs cd4CountObs = EmrUtils.firstObsInEncounter(encounter, cd4CountConcept);
					qualified = (whoStageObs != null && whoStageObs.getValueCoded() != null)
							|| (cd4CountObs != null && cd4CountObs.getValueNumeric() != null);
				}
			}
			resultMap.put(ptId, new BooleanResult(qualified, this, context));
		}

		return resultMap;
	}
}
