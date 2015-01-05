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
	}
}