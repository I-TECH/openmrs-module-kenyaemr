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

package org.openmrs.module.kenyaemr.fragment.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.ScheduledVisitOnDayCalculation;
import org.openmrs.module.kenyaemr.calculation.library.VisitsOnDayCalculation;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.util.PersonByNameComparator;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for daily schedule fragment
 */
public class DailyScheduleFragmentController {
	
	public void controller(FragmentModel model, @FragmentParam(value = "date", required = false) Date date) {

		Date today = OpenmrsUtil.firstSecondOfDay(new Date());
		Date tomorrow = CoreUtils.dateAddDays(today, 1);
		Date yesterday = CoreUtils.dateAddDays(today, -1);

		// Date defaults to today
		if (date == null) {
			date = today;
		}
		else {
			// Ignore time
			date = OpenmrsUtil.firstSecondOfDay(date);
		}

		model.addAttribute("date", date);
		model.addAttribute("isToday", date.equals(today));
		model.addAttribute("isTomorrow", date.equals(tomorrow));
		model.addAttribute("isYesterday", date.equals(yesterday));
	}

	/**
	 * Gets scheduled patients
	 * @param date the date
	 * @param ui the UI utils
	 * @return the simplified patients
	 */
	public List<SimpleObject> scheduledPatients(@RequestParam("date") Date date, UiUtils ui, @SpringBean KenyaUiUtils kenyaUi) {
		// Run the calculations to get patients with scheduled visits
		PatientCalculationService cs = Context.getService(PatientCalculationService.class);
		Set<Integer> allPatients = Context.getPatientSetService().getAllPatients().getMemberIds();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", date);
		PatientCalculationContext calcContext = cs.createCalculationContext();

		Set<Integer> scheduled = CalculationUtils.patientsThatPass(cs.evaluate(allPatients, new ScheduledVisitOnDayCalculation(), params, calcContext));
		CalculationResultMap actual = cs.evaluate(scheduled, new VisitsOnDayCalculation(), params, calcContext);

		// Sort patients and convert to simple objects
		List<Patient> scheduledPatients = Context.getPatientSetService().getPatients(scheduled);
		Collections.sort(scheduledPatients, new PersonByNameComparator());

		List<SimpleObject> simplified = new ArrayList<SimpleObject>();
		for (Patient p : scheduledPatients) {
			SimpleObject so = ui.simplifyObject(p);

			ListResult visitsResult = (ListResult) actual.get(p.getPatientId());
			List<Visit> visits = EmrCalculationUtils.extractListResultValues(visitsResult);

			if (visits.size() > 0) {
				StringBuilder extra = new StringBuilder();

				for (Visit visit : visits) {
					extra.append("<div class='ke-tag ke-visittag'>" + ui.format(visit.getVisitType()) + "<br/><small>" + kenyaUi.formatTime(visit.getStartDatetime()) + "</small></div>");
				}

				so.put("extra", extra);
			}

			simplified.add(so);
		}

		return simplified;
	}
}