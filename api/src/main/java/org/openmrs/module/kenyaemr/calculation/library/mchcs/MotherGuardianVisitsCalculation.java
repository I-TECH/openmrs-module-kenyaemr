/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
