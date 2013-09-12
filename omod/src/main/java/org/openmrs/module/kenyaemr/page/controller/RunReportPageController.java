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

package org.openmrs.module.kenyaemr.page.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.module.kenyacore.report.IndicatorReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportManager;
import org.openmrs.module.kenyaemr.util.EmrUiUtils;
import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Run report page
 */
@SharedPage
public class RunReportPageController {
	
	public void controller(@RequestParam("reportId") String reportId,
						   @RequestParam(required = false, value = "startDate") Date startDate,
						   @RequestParam("returnUrl") String returnUrl,
						   PageRequest pageRequest,
						   PageModel model,
						   @SpringBean ReportManager reportManager,
						   @SpringBean EmrUiUtils emrUi) throws Exception {

		ReportDescriptor report = reportManager.getReportDescriptor(reportId);
		emrUi.checkReportAccess(pageRequest, report);

		boolean isIndicator = report instanceof IndicatorReportDescriptor;
		boolean isExcelRenderable = isIndicator && ((IndicatorReportDescriptor) report).getTemplate() != null;

		model.addAttribute("report", report);
		model.addAttribute("isIndicator", isIndicator);
		model.addAttribute("excelRenderable", isExcelRenderable);
		model.addAttribute("returnUrl", returnUrl);

		if (isIndicator) {
			Map<String, String> startDateOptions = new LinkedHashMap<String, String>();
			SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat pretty = new SimpleDateFormat("MMMM yyyy");
			Date d = DateUtil.getStartOfMonth(new Date());
			for (int i = 0; i < 6; ++i) {
				d = DateUtil.getStartOfMonth(d, -1);
				startDateOptions.put(ymd.format(d), pretty.format(d));
			}

			model.addAttribute("startDateOptions", startDateOptions);
			model.addAttribute("startDateSelected", startDate != null ? ymd.format(startDate) : null);
			model.addAttribute("startDate", startDate);
		}
	}
}