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

import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Returns a list of infant-mother/guardian pair visits
 */
public class InfantMotherOrGuardianPairVisitsCalculation extends AbstractPatientCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		Map<String, Object> params = new HashMap<String, Object>();
		Integer reviewPeriod = (Integer)parameterValues.get("reviewPeriod");
		params.put("reviewPeriod", reviewPeriod);

		CalculationResultMap motherGuardianVisits = Context.getService(PatientCalculationService.class).evaluate(cohort, new MotherGuardianVisitsCalculation(), params, context );
		CalculationResultMap infantVisits = Context.getService(PatientCalculationService.class).evaluate(cohort, new VisitsWithinAPeriodCalculation(), params, context );
		CalculationResultMap infantRelations = calculate(new InfantMotherGuardianRelationsCalculation(), cohort, context);

		//extract infant and mother/guardian ids from calculation map
		Set<Integer> infantIds = infantVisits.keySet();

		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptid : cohort){
			// create containers for infant and mother/guardian visits for every cohort member
			Set<Date> infantVisitDates = new HashSet<Date>();
			Set<Date> motherVisitDates = new HashSet<Date>();

			boolean result = false;
			if (infantIds.contains(ptid)){
				ListResult infantData = (ListResult)infantVisits.get(ptid);
				List<Visit> ivisits = CalculationUtils.extractResultValues(infantData);
				for (Visit iv: ivisits){
					infantVisitDates.add(extractDateFromDateTime(iv.getStartDatetime()));
				}

				Set<Integer> mother_guardian_ids = (Set<Integer>)infantRelations.get(ptid).getValue();

				for(Integer id : mother_guardian_ids) {
					ListResult mother_visits_data = (ListResult)motherGuardianVisits.get(id);
					List<Visit> mother_visits = CalculationUtils.extractResultValues(mother_visits_data);

					for (Visit mv : mother_visits){
						motherVisitDates.add(extractDateFromDateTime(mv.getStartDatetime()));
					}
				}

				if (!Collections.disjoint(infantVisitDates, motherVisitDates)){
					result = true;
				}
				ret.put(ptid, new BooleanResult(result, this));

			}
		}
		
		CalculationUtils.ensureEmptyListResults(ret, cohort);
		return ret;
	}

	private Date extractDateFromDateTime(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.clear(Calendar.HOUR);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.MILLISECOND);
		return cal.getTime();

	}

}
