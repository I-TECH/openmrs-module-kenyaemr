/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.rdqa;

import org.openmrs.Encounter;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Gets date of the last encounter
 */
public class PatientLastEncounterDateCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		CalculationResultMap data = Calculations.lastEncounter(null, cohort, context);
		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptid : cohort) {
			Date lastEncounterDate = null;
			if (data.get(ptid) != null){
				SimpleResult res = (SimpleResult)data.get(ptid);
				Encounter e = (Encounter) res.getValue();

				if (e != null){
					lastEncounterDate = e.getEncounterDatetime();
				}
			}

			ret.put(ptid, new SimpleResult(lastEncounterDate, this));
		}
		return ret;
	}
}
