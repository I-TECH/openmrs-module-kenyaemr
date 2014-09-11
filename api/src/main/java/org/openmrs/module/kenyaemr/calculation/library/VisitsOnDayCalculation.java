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

package org.openmrs.module.kenyaemr.calculation.library;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.reporting.common.DateUtil;

/**
 * Returns a list of visits each patient had on a particular date
 */
public class VisitsOnDayCalculation extends AbstractPatientCalculation {

	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues, PatientCalculationContext context) {
		Date date = (Date) parameterValues.get("date");
		if (date == null) {
			date = new Date();
		}
		Date startOfDay = DateUtil.getStartOfDay(date);
		Date endOfDay = DateUtil.getEndOfDay(date);
		
		List<Patient> patientStubs = new ArrayList<Patient>();
		for (Integer ptId : cohort) {
			patientStubs.add(new Patient(ptId));
		}
		
		// look for visits that started before endOfDay and ended after startOfDay 
		List<Visit> visits = Context.getVisitService().getVisits(null, patientStubs, null, null, null, endOfDay, startOfDay, null, null, true, false);
		
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
	
}
