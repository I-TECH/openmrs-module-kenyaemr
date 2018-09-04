/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
