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

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;

import java.util.Collection;
import java.util.Map;

/**
 * Return the period that we are running the report for
 */
public class CohortReportTypeCalculation extends AbstractPatientCalculation {
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Integer months = (parameterValues != null && parameterValues.containsKey("months")) ? (Integer) parameterValues.get("months") : null;

		CalculationResultMap ret = new CalculationResultMap();

		for(Integer ptId:cohort) {
			String reportType = "ART Cohort Analysis("+ months +")";
			ret.put(ptId, new SimpleResult(reportType, this));
		}
		return ret;
	}
}
