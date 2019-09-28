/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

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
		CalculationResultMap ldl = Calculations.allObs(Dictionary.getConcept(Dictionary.HIV_VIRAL_LOAD_QUALITATIVE), cohort, context);

		for(Integer ptId : cohort){

			boolean hasVLResultsXMonthsAgo = false;

			ListResult vlObsListResult = (ListResult) allVlsResults.get(ptId);
			ListResult ldlObsListResult = (ListResult) ldl.get(ptId);

			if (vlObsListResult != null) {
				List<Obs> obsList = CalculationUtils.extractResultValues(vlObsListResult);

					if (obsList.size() > 0) {
						for (Obs obs : obsList) {
							Date obsDate = obs.getObsDatetime();
							if (obsDate.after(DateUtil.adjustDate(dateMonthsAgo.getTime(), -1, DurationUnit.DAYS)) && obsDate.before(context.getNow())) {
								hasVLResultsXMonthsAgo = true;
							}
						}
					}
			}

			if(ldlObsListResult != null){
				List<Obs> ldlList = CalculationUtils.extractResultValues(ldlObsListResult);
				for (Obs ldlObs : ldlList) {
					Date obsDate = ldlObs.getObsDatetime();
					if (obsDate.after(DateUtil.adjustDate(dateMonthsAgo.getTime(), -1, DurationUnit.DAYS)) && obsDate.before(context.getNow())) {
						hasVLResultsXMonthsAgo = true;
					}
				}
			}
			ret.put(ptId, new BooleanResult(hasVLResultsXMonthsAgo, this, context));
		}
		return ret;

	}
}
