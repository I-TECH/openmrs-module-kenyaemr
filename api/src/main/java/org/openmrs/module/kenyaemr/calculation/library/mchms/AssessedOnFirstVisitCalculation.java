/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.mchms;

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
import org.openmrs.module.kenyaemr.wrapper.EncounterWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyaemr.ArtAssessmentMethod;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;

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
public class AssessedOnFirstVisitCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		ArtAssessmentMethod artAssessmentMethod = (params != null && params.containsKey("artAssessmentMethod")) ?
				(ArtAssessmentMethod) params.get("artAssessmentMethod") : null;

		Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);
		EncounterType mchConsultation = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION);
		Concept whoStageConcept = Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE);
		Concept cd4CountConcept = Dictionary.getConcept(Dictionary.CD4_COUNT);

		// Get all patients who are alive and in MCH-MS program
		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inMchmsProgram = Filters.inProgram(mchmsProgram, alive, context);

		CalculationResultMap crm = Calculations.firstEncounter(mchConsultation, inMchmsProgram, context);

		CalculationResultMap resultMap = new CalculationResultMap();

		for (Integer ptId : cohort) {
			boolean qualified = false;
			Encounter encounter = EmrCalculationUtils.encounterResultForPatient(crm, ptId);

			if (encounter != null) {
				EncounterWrapper wrapper = new EncounterWrapper(encounter);

				if (artAssessmentMethod == ArtAssessmentMethod.WHO_STAGING) {
					Obs whoStageObs = wrapper.firstObs(whoStageConcept);
					qualified = whoStageObs != null && whoStageObs.getValueCoded() != null;
				}
				else if (artAssessmentMethod == ArtAssessmentMethod.CD4_COUNT) {
					Obs cd4CountObs = wrapper.firstObs(cd4CountConcept);
					qualified = cd4CountObs != null && cd4CountObs.getValueNumeric() != null;
				}
				else {
					Obs whoStageObs = wrapper.firstObs(whoStageConcept);
					Obs cd4CountObs = wrapper.firstObs(cd4CountConcept);
					qualified = (whoStageObs != null && whoStageObs.getValueCoded() != null)
							|| (cd4CountObs != null && cd4CountObs.getValueNumeric() != null);
				}
			}
			resultMap.put(ptId, new BooleanResult(qualified, this, context));
		}

		return resultMap;
	}
}
