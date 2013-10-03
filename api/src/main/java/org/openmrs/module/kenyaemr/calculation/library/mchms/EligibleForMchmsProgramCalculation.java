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

import org.openmrs.Person;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;

import java.util.Collection;
import java.util.Map;

/**
 * Calculates whether patients are eligible for the MCH mother services program
 */
public class EligibleForMchmsProgramCalculation extends BaseEmrCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
		CalculationResultMap ret = new CalculationResultMap();

		for (int ptId : cohort) {
			// TODO rework so that we don't have to load each patient from database individually
			PersonService personService = Context.getPersonService();
			Person person = personService.getPerson(ptId);
			if (person.getGender().equals("F")) {
				ret.put(ptId, new BooleanResult(true, this));
			}
		}
		return ret;
	}
}