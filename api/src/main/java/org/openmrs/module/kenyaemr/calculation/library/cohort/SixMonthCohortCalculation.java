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
package org.openmrs.module.kenyaemr.calculation.library.cohort;

import org.openmrs.Obs;
import org.openmrs.api.ObsService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyaemr.Dictionary;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Calculates cohort of a patient using difference between encounter and next visit dates
 *
 */
public class SixMonthCohortCalculation extends AbstractPatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {

		ObsService service = Context.getObsService();
		PersonService personService = Context.getPersonService();
		Set<Integer> newCohort = new HashSet<Integer>();

		List<Obs> rtcPatientsObs = service.getObservations(null, null, Arrays.asList(Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE)), null, null, null, null, null, null, context.getNow(), null, false);
		for (Obs o : rtcPatientsObs) {
			newCohort.add(o.getPersonId());
		}
		CalculationResultMap ret = new CalculationResultMap();

		if (newCohort.isEmpty()) {
			return ret;
		}

		for (Integer ptid : newCohort) {

			List<Obs> rtc = service.getObservations(Arrays.asList(personService.getPerson(ptid)), null, Arrays.asList(Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE)), null, null, null, null, null, null, context.getNow(), null, false);
			if (!rtc.isEmpty()) {
				PatientCohortCategoryInfo cohortInfo = CohortReportUtil.getPatientCohortCategoryInfo(rtc.get(0).getObsDatetime(), rtc.get(0).getValueDate());
				if (cohortInfo != null && cohortInfo.getCohort() != null && cohortInfo.getUnit() != null) {
					if (6 == cohortInfo.getCohort() && "Month".equals(cohortInfo.getUnit())) {
						ret.put(ptid, new SimpleResult(cohortInfo, this));
					}
				}
			}
		}

		return ret;
	}
}
