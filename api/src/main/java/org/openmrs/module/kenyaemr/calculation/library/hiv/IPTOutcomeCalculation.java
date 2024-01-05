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
import org.openmrs.module.kenyaemr.metadata.IPTMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Calculates the last IPT outcome
 */
public class IPTOutcomeCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		EncounterService encService = Context.getEncounterService();
		PatientService patientService = Context.getPatientService();
		EncounterType et = encService.getEncounterTypeByUuid(IPTMetadata._EncounterType.IPT_OUTCOME);
		String iptOutcomeConcept = "161555AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";


		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {

			Encounter lastIptOutcome = EmrUtils.lastEncounter(patientService.getPatient(ptId), et);
			Concept codedOutcome = null;
			if (lastIptOutcome != null) {
				for (Obs o : lastIptOutcome.getObs()) {
					if (o.getConcept().getUuid().equals(iptOutcomeConcept)) {
						codedOutcome = o.getValueCoded();
						break;
					}
				}
			}
			ret.put(ptId, new SimpleResult(codedOutcome, this, context));

		}

		return  ret;
	}
}
