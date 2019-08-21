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

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Calculate the bmi at last visit
 */
public class BMIAtLastVisitCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		EncounterService encService = Context.getEncounterService();
		PatientService patientService = Context.getPatientService();
		EncounterType et = encService.getEncounterTypeByUuid(CommonMetadata._EncounterType.TRIAGE);
		String heightConcept = "5090AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		String weightConcept = "5089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";


		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {

			Double visitWeight = null;
			Double visitHeight = null;
			String bmiStr = null;

			Encounter lastTriageEncounter = EmrUtils.lastEncounter(patientService.getPatient(ptId), et);
			if (lastTriageEncounter != null) {
				for (Obs o : lastTriageEncounter.getObs()) {
					if (o.getConcept().getUuid().equals(heightConcept)) {
						visitHeight = o.getValueNumeric();
					} else if (o.getConcept().getUuid().equals(weightConcept)) {
						visitWeight = o.getValueNumeric();
					}
				}
			}
			if (visitHeight != null && visitWeight != null) {
				Double bmi = visitWeight / ((visitHeight/100) * (visitHeight/100));
				bmiStr = String.format("%.2f", bmi);
			}

			ret.put(ptId, new SimpleResult(bmiStr, this, context));

		}

		return  ret;
	}
}
