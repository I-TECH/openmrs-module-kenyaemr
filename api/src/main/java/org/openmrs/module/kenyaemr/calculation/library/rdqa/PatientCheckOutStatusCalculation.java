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
