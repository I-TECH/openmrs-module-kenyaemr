/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.BaseEmrCalculation;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

/**
 * Calculates the date on which a patient first started ART
 */
public class InitialArtStartDateCalculation extends BaseEmrCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @should return null for patients who have not started ART
	 * @should return start date for patients who have started ART
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
	                                     PatientCalculationContext context) {

		// Get earliest dates from orders
		Concept arvs = Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_DRUGS);
		CalculationResultMap allDrugOrders = allDrugOrders(arvs, cohort, context);
		CalculationResultMap earliestOrderDates = earliestStartDates(allDrugOrders, context);

		// Return the earliest of the two
		CalculationResultMap result = new CalculationResultMap();
		for (Integer ptId : cohort) {
			Date orderDate = EmrCalculationUtils.datetimeResultForPatient(earliestOrderDates, ptId);

			result.put(ptId, orderDate == null ? null : new SimpleResult(orderDate, null));
		}
		return result;
	}
}