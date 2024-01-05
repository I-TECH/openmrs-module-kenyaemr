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
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculate the date a client was confirmed HIV positive
 */
public class DateConfirmedHivPositiveCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		EncounterService encService = Context.getEncounterService();
		PatientService patientService = Context.getPatientService();
		EncounterType et = encService.getEncounterTypeByUuid(HivMetadata._EncounterType.HIV_ENROLLMENT);
		String dateConfirmedPositiveConcept = "160554AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";


		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : cohort) {

			Encounter firstHivEnrollment = EmrUtils.firstEncounter(patientService.getPatient(ptId), et);
			Date dateConfirmed = null;
			if (firstHivEnrollment != null) {
				for (Obs o : firstHivEnrollment.getObs()) {
					if (o.getConcept().getUuid().equals(dateConfirmedPositiveConcept)) {
						dateConfirmed = o.getValueDatetime();
						break;
					}
				}
			}
			ret.put(ptId, new SimpleResult(dateConfirmed, this, context));

		}

		return  ret;
	}
}
