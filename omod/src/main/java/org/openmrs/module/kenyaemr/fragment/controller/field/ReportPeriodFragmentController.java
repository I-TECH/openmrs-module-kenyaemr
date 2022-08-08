/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.field;

import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller for report period field fragments
 */
public class ReportPeriodFragmentController {

	public void controller(@FragmentParam("pastMonths") int pastMonths,
							@FragmentParam("pastYears") int pastYears,
						   FragmentModel model,
						   @SpringBean KenyaUiUtils kenyaui) {

		List<SimpleObject> months = new ArrayList<SimpleObject>();
		SimpleDateFormat labelFormat = new SimpleDateFormat("MMM yyyy");

		Date start = DateUtil.getStartOfMonth(new Date());
		for (int i = 0; i < pastMonths; ++i) {
			start = DateUtil.getStartOfMonth(start, -1);
			Date end = DateUtil.getEndOfMonth(start);

			months.add(SimpleObject.create(
					"label", labelFormat.format(start),
					"range", kenyaui.formatDateParam(start) + "|" + kenyaui.formatDateParam(end)
			));
		}
		model.addAttribute("months", months);
		
		List<SimpleObject> years = new ArrayList<SimpleObject>();
		labelFormat = new SimpleDateFormat("yyyy");

		start = DateUtil.getStartOfPeriod(new Date(), 4);
		for (int i = 0; i < pastYears; ++i) {
			Date end = DateUtil.getEndOfPeriod(start, 4);

			years.add(SimpleObject.create(
					"label", labelFormat.format(start),
					"range", kenyaui.formatDateParam(start) + "|" + kenyaui.formatDateParam(end)
			));
			start = DateUtil.getStartOfMonth(start, -1);
			start = DateUtil.getStartOfPeriod(start, 4);

		}
		model.addAttribute("years", years);
	}
}