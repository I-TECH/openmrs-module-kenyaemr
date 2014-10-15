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

package org.openmrs.module.kenyaemr.calculation.library.mchcs;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Returns a list of visits each patient had during a given period of time
 */
public class VisitsWithinAPeriodCalculation extends AbstractPatientCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
		Integer reviewPeriod = (Integer) parameterValues.get("reviewPeriod"); //duration between nth month - end of reporting period

		Date endDate = context.getNow();
		Calendar startDate = Calendar.getInstance();
		startDate.clear(Calendar.HOUR);
		startDate.clear(Calendar.MINUTE);
		startDate.clear(Calendar.MILLISECOND);
		startDate.setTime(endDate);
		startDate.add(Calendar.MONTH, -(reviewPeriod));
		Date beginningOfReviewPeriod = startDate.getTime();

		Set<Patient> patients = getPatientSetFromIds(cohort);
		// look for visits between beginningOfReviewPeriod and endDate
		List<Visit> visits = Context.getVisitService().getVisits(null, patients, null, null, beginningOfReviewPeriod, null, null, endDate, null, true, false);

		// organize by patient
		CalculationResultMap ret = new CalculationResultMap();
		for (Visit v : visits) {
			Integer ptId = v.getPatient().getId();
			ListResult holder = (ListResult) ret.get(ptId);
			if (holder == null) {
				holder = new ListResult();
				ret.put(ptId, holder);
			}
			holder.add(new SimpleResult(v, this));
		}
		
		CalculationUtils.ensureEmptyListResults(ret, cohort);
		return ret;
	}

	private Set<Patient> getPatientSetFromIds(Collection<Integer> cohort){
		PatientService service = Context.getPatientService();

		Set<Patient> patients = new HashSet<Patient>();
		for (Integer id :cohort ){
			patients.add(service.getPatient(id));
		}
		return patients;
	}

}
