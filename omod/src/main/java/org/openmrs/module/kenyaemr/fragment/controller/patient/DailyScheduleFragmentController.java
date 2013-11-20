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

package org.openmrs.module.kenyaemr.fragment.controller.patient;

import java.util.Date;

import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.OpenmrsUtil;

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
}