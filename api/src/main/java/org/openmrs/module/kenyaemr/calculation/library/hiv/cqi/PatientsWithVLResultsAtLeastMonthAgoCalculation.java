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

import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * patients with VL results at least X months ago
 */
public class PatientsWithVLResultsAtLeastMonthAgoCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

		Integer months = (params != null && params.containsKey("months")) ? (Integer) params.get("months") : null;

		//get date that is months ago
		Calendar dateMonthsAgo = Calendar.getInstance();
		dateMonthsAgo.setTime(context.getNow());
		dateMonthsAgo.add(Calendar.MONTH, -months);

		CalculationResultMap ret = new CalculationResultMap();

		CalculationResultMap allVlsResults = Calculations.allObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD), cohort, context);

		for(Integer ptId : cohort){

			boolean hasVLResultsXMonthsAgo = false;

			ListResult vlObsListResult = (ListResult) allVlsResults.get(ptId);

			if (vlObsListResult != null) {
				List<Obs> obsList = CalculationUtils.extractResultValues(vlObsListResult);

					if (obsList.size() > 0) {
						for (Obs obs : obsList) {
							Date obsDate = obs.getObsDatetime();
							if (obsDate.after(dateMonthsAgo.getTime()) && obsDate.before(context.getNow())) {
								hasVLResultsXMonthsAgo = true;
								//break;
							}
						}
					}
			}
			ret.put(ptId, new BooleanResult(hasVLResultsXMonthsAgo, this, context));
		}
		return ret;

	}
}
