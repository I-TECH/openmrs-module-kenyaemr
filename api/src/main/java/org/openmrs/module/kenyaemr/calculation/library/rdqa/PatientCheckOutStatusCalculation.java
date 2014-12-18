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

import org.openmrs.Visit;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.report.data.patient.definition.VisitsForPatientDataDefinition;
import org.openmrs.module.reporting.common.TimeQualifier;

import java.util.Collection;
import java.util.Map;

/**
 * Gets the last visit and checks if a patient was checked out
 */
public class PatientCheckOutStatusCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		VisitsForPatientDataDefinition definition = new VisitsForPatientDataDefinition();
		definition.setWhich(TimeQualifier.LAST);
		CalculationResultMap data = CalculationUtils.evaluateWithReporting(definition, cohort, parameterValues, null, context);
		CalculationResultMap ret = new CalculationResultMap();

		for (Integer ptid : cohort) {
			boolean isCheckedOut = false;
			if (data.get(ptid) != null){
				SimpleResult res = (SimpleResult)data.get(ptid);
				Visit v = (Visit) res.getValue();

				if (v.getStopDatetime() != null){
					isCheckedOut = true;
				}
			}

			ret.put(ptid, new BooleanResult(isCheckedOut, this, context));
		}
		return ret;
	}
}
