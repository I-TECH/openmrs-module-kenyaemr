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
