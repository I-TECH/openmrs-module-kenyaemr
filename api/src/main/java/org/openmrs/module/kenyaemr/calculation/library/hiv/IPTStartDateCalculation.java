/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Calculate the date when IPT was started
 */
public class IPTStartDateCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();
		CalculationResultMap iptStartDateMap = Calculations.firstObs(Dictionary.getConcept(Dictionary.ISONIAZID_DISPENSED), cohort, context);

		for(Integer ptId:cohort) {
			Date iptStartDate = null;

			Obs fastObs = EmrCalculationUtils.obsResultForPatient(iptStartDateMap, ptId);
			if(fastObs != null && fastObs.getConcept().equals(Dictionary.YES)) {
				iptStartDate = fastObs.getObsDatetime();
			}

			ret.put(ptId, new SimpleResult(iptStartDate, this));
		}

		return ret;
	}
}
