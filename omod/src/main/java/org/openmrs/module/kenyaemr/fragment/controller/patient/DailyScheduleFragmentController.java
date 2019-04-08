/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.patient;

import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.OpenmrsUtil;

import java.util.Date;

/**
 * Controller for daily schedule fragment
 */
public class DailyScheduleFragmentController {
	
	public void controller(FragmentModel model, @FragmentParam(value = "date", required = false) Date date) {

		Date today = OpenmrsUtil.firstSecondOfDay(new Date());
		Date tomorrow = CoreUtils.dateAddDays(today, 1);
		Date yesterday = CoreUtils.dateAddDays(today, -1);
		Date past = CoreUtils.dateAddDays(yesterday, -1);

		boolean isFuture = false;
		if(date.after(tomorrow)){
			isFuture = true;
		}

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
		model.addAttribute("isFuture", isFuture);

	}
}