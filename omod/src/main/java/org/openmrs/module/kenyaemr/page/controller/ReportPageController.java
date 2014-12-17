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
import java.util.List;
import java.util.Map;

import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.report.HybridReportDescriptor;
import org.openmrs.module.kenyacore.report.IndicatorReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportManager;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Report overview page
 */
@SharedPage
public class ReportPageController {
	
	public void get(@RequestParam("reportUuid") String reportUuid,
					@RequestParam(required = false, value = "startDate") Date startDate,
					@RequestParam("returnUrl") String returnUrl,
					PageRequest pageRequest,
					PageModel model,
					UiUtils ui,
					@SpringBean ReportManager reportManager,
					@SpringBean KenyaUiUtils kenyaUi,
					@SpringBean ReportService reportService,
					@SpringBean ReportDefinitionService definitionService) throws Exception {

		ReportDefinition definition = definitionService.getDefinitionByUuid(reportUuid);
		ReportDescriptor report = reportManager.getReportDescriptor(definition);

		CoreUtils.checkAccess(report, kenyaUi.getCurrentApp(pageRequest));

		boolean isIndicator = false;
		if (report instanceof IndicatorReportDescriptor || report instanceof HybridReportDescriptor)
			isIndicator = true;

		boolean excelRenderable = false;
		if (report instanceof IndicatorReportDescriptor && isIndicator && ((IndicatorReportDescriptor) report).getTemplate() != null) {
			excelRenderable = true;
		} else if (report instanceof HybridReportDescriptor && isIndicator && ((HybridReportDescriptor) report).getTemplate() != null) {
			excelRenderable = true;
		}

		model.addAttribute("report", report);
		model.addAttribute("definition", definition);
		model.addAttribute("isIndicator", isIndicator);
		model.addAttribute("excelRenderable", excelRenderable);
		model.addAttribute("returnUrl", returnUrl);

		if (isIndicator) {
			Map<String, String> startDateOptions = new LinkedHashMap<String, String>();
			SimpleDateFormat pretty = new SimpleDateFormat("MMMM yyyy");
			Date d = DateUtil.getStartOfMonth(new Date());
			for (int i = 0; i < 6; ++i) {
				d = DateUtil.getStartOfMonth(d, -1);
				startDateOptions.put(kenyaUi.formatDateParam(d), pretty.format(d));
			}

			model.addAttribute("startDateOptions", startDateOptions);
			model.addAttribute("startDateSelected", startDate != null ? kenyaUi.formatDateParam(startDate) : null);
			model.addAttribute("startDate", startDate);
		}

		model.addAttribute("requests", getRequests(definition, ui, reportService));
	}

	/**
	 * Gets the existing requests for the given report
	 * @param definition the report definition
	 * @param ui the UI utils
	 * @param reportService the report service
	 * @return the simplified requests
	 */
	public SimpleObject[] getRequests(ReportDefinition definition, UiUtils ui, ReportService reportService) {
		List<ReportRequest> requests = reportService.getReportRequests(definition, null, null, null);
		return ui.simplifyCollection(requests);
	}
}