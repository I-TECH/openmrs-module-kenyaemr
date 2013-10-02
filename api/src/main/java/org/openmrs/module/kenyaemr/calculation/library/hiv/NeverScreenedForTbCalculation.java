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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;

/**
 * Calculates HIV patients who have not been screened for TB
 */
public class NeverScreenedForTbCalculation extends BaseEmrCalculation {

	/**
	 * Evaluates the calculation
	 * @param cohort the patient cohort
	 * @param params the calculation parameters
	 * @param context the calculation context
	 * @return the result map
	 * @should calculate null for patients who are not enrolled in the HIV program or not alive
	 * @should calculate true for patients who have no TB screening encounter
	 * @should calculate false for patients who have a TB screening encounter
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
		Program hivProgram = MetadataUtils.getProgram(HivMetadata._Program.HIV);

		Concept tbDiseaseStatus = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS);
		Concept diseaseSuspected = Dictionary.getConcept(Dictionary.DISEASE_SUSPECTED);
		Concept diseaseDiagnosed = Dictionary.getConcept(Dictionary.DISEASE_DIAGNOSED);
		Concept noSignsOrSymptoms = Dictionary.getConcept(Dictionary.NO_SIGNS_OR_SYMPTOMS_OF_DISEASE);

		Set<Integer> alive = alivePatients(cohort, context);
		Set<Integer> inHivProgram = CalculationUtils.patientsThatPass(Calculations.activeEnrollment(hivProgram, alive, context));

		CalculationResultMap screeningObs = Calculations.allObs(tbDiseaseStatus, cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean hivAndNeverScreened = false;

			if (inHivProgram.contains(ptId)) {
				hivAndNeverScreened = true;

				List<Obs> diseasesStatuses = EmrCalculationUtils.extractListResultValues((ListResult) screeningObs.get(ptId));

				for (Obs diseaseStatus : diseasesStatuses) {
					if (diseaseSuspected.equals(diseaseStatus.getValueCoded()) || diseaseDiagnosed.equals(diseaseStatus.getValueCoded()) || noSignsOrSymptoms.equals(diseaseStatus.getValueCoded())) {
						hivAndNeverScreened = false;
						break;
					}
				}
			}

			ret.put(ptId, new BooleanResult(hivAndNeverScreened, this, context));
		}
		return ret;
	}
}