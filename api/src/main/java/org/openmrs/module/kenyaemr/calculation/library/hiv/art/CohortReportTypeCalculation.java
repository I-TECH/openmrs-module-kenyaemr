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
