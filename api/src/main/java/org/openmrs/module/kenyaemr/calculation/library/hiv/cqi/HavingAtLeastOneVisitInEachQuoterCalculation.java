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
package org.openmrs.module.kenyaemr.calculation.library.hiv.cqi;

import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.report.data.patient.definition.VisitsForPatientDataDefinition;
import org.openmrs.module.reporting.common.TimeQualifier;

import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

/**
 *  is if the patient just appears during the 1st quarter (say any time btwn Jan-Mar),
 *  and also any other time during the 2nd quarter (say any time btwn Apr-June)
 *  they should as well be counted in the numerator:-
 *  one good example here for 2.1 is if there is a visit in Mar2013 and then April2013,
 *  even if we don't have another visit, the condition is met (review period 30June2013) -
 *  simply by the mere fact that they visited within Q1 and visited within Q2 as well.
 */
public class HavingAtLeastOneVisitInEachQuoterCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		CalculationResultMap ret = new CalculationResultMap();

		Calendar endOfReportingPeriod = Calendar.getInstance();
		endOfReportingPeriod.setTime(context.getNow());

		Calendar startDateInQ2 = Calendar.getInstance();
		startDateInQ2.setTime(endOfReportingPeriod.getTime());
		startDateInQ2.add(Calendar.MONTH, -3);

		Calendar startDateInQ1 = Calendar.getInstance();
		startDateInQ1.setTime(endOfReportingPeriod.getTime());
		startDateInQ1.add(Calendar.MONTH, -6);

		Calendar endDateInQ1 = Calendar.getInstance();
		endDateInQ1.setTime(startDateInQ1.getTime());
		endDateInQ1.add(Calendar.MONTH, 3);


		//find visits that happened in first quoter
		VisitsForPatientDataDefinition visitsDefInQ1 = new VisitsForPatientDataDefinition();
		visitsDefInQ1.setWhich(TimeQualifier.ANY);
		visitsDefInQ1.setStartedOnOrAfter(startDateInQ1.getTime());
		visitsDefInQ1.setStartedOnOrBefore(endDateInQ1.getTime());

		//find visits that happened in second quoter
		VisitsForPatientDataDefinition visitsDefInQ2 = new VisitsForPatientDataDefinition();
		visitsDefInQ2.setWhich(TimeQualifier.ANY);
		visitsDefInQ2.setStartedOnOrAfter(startDateInQ2.getTime());
		visitsDefInQ2.setStartedOnOrBefore(endOfReportingPeriod.getTime());

		CalculationResultMap visitDataInQ1 = CalculationUtils.evaluateWithReporting(visitsDefInQ1, cohort, params, null, context);

		CalculationResultMap visitDataInQ2 = CalculationUtils.evaluateWithReporting(visitsDefInQ2, cohort, params, null, context);

		for (Integer ptId: cohort) {
			boolean has1VisitInEveryQuoter = false;

			ListResult resultQ1 = (ListResult) visitDataInQ1.get(ptId);
			ListResult resultQ2 = (ListResult) visitDataInQ2.get(ptId);

				if(resultQ1 != null && resultQ2 != null){
					has1VisitInEveryQuoter = true;
				}
			ret.put(ptId, new BooleanResult(has1VisitInEveryQuoter, this, context));
		}

		return ret;
	}
}
