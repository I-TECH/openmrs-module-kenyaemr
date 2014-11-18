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

package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Takes a cohort of patients and returns mother/guardian visits
 */
public class MotherGuardianVisitsCalculation extends AbstractPatientCalculation {
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		//create cohort of mothers and guardians from InfantMotherGuardianRelationsCalculation
		CalculationResultMap infantRelationsResultMap = calculate(new InfantMotherGuardianRelationsCalculation(), cohort, context);

		// define container for new cohort
		Set<Integer> all_parent_guardian_ids = new HashSet<Integer>();

		for (Integer id : cohort) {
			all_parent_guardian_ids.addAll((Set<Integer>)infantRelationsResultMap.get(id).getValue());
		}

		//get relevant visits for mother and guardian relations

		Map<String, Object> params = new HashMap<String, Object>();
		Integer reviewPeriod = (Integer)parameterValues.get("reviewPeriod");
		params.put("reviewPeriod", reviewPeriod);

		CalculationResultMap motherGuardianVisits = Context.getService(PatientCalculationService.class).evaluate(all_parent_guardian_ids, new VisitsWithinAPeriodCalculation(), params, context );
		CalculationUtils.ensureEmptyListResults(motherGuardianVisits, all_parent_guardian_ids);

		return motherGuardianVisits;

		}


}
