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
