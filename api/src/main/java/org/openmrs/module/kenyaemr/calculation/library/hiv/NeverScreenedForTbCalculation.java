/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyacore.calculation.Filters;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculates HIV patients who have not been screened for TB
 */
public class NeverScreenedForTbCalculation extends AbstractPatientCalculation {

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
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);

		Concept tbDiseaseStatus = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS);
		Concept diseaseSuspected = Dictionary.getConcept(Dictionary.DISEASE_SUSPECTED);
		Concept diseaseDiagnosed = Dictionary.getConcept(Dictionary.DISEASE_DIAGNOSED);
		Concept noSignsOrSymptoms = Dictionary.getConcept(Dictionary.NO_SIGNS_OR_SYMPTOMS_OF_DISEASE);

		Set<Integer> alive = Filters.alive(cohort, context);
		Set<Integer> inHivProgram = Filters.inProgram(hivProgram, alive, context);
		Set<Integer> inTbProgram = Filters.inProgram(tbProgram , alive ,context);

		CalculationResultMap screeningObs = Calculations.allObs(tbDiseaseStatus, cohort, context);

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {
			boolean hivAndNeverScreened = false;

			if (inHivProgram.contains(ptId)) {
				hivAndNeverScreened = true;

				List<Obs> diseasesStatuses = CalculationUtils.extractResultValues((ListResult) screeningObs.get(ptId));

				for (Obs diseaseStatus : diseasesStatuses) {
					if (diseaseSuspected.equals(diseaseStatus.getValueCoded()) || diseaseDiagnosed.equals(diseaseStatus.getValueCoded()) || noSignsOrSymptoms.equals(diseaseStatus.getValueCoded()) || inTbProgram.contains(ptId) ) {
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