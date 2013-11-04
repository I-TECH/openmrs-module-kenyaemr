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

package org.openmrs.module.kenyaemr.fragment.controller.report;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportManager;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaui.annotation.SharedAction;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.web.renderers.DefaultWebRenderer;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * AJAX utility methods for reports
 */
public class ReportUtilsFragmentController {

	protected static final Log log = LogFactory.getLog(ReportUtilsFragmentController.class);

	/**
	 * Requests a report evaluation
	 * @param reportId the report descriptor id
	 * @param startDate the start date (optional)
	 * @param reportManager the report manager
	 * @return the report request id
	 */
	public SimpleObject requestReport(@RequestParam("reportId") String reportId,
									  @RequestParam(required = false, value = "startDate") Date startDate,
									  UiUtils ui,
									  @SpringBean ReportService reportService,
									  @SpringBean ReportManager reportManager) {

		ReportDescriptor report = reportManager.getReportDescriptor(reportId);

		// TODO check access permissions

		ReportDefinition definition = reportManager.getReportDefinition(report);
		Mapped<ReportDefinition> mappedDefinition;

		if (startDate != null) {
			mappedDefinition = ReportUtils.map(definition, "startDate", startDate, "endDate", DateUtil.getEndOfMonth(startDate));
		} else {
			mappedDefinition = ReportUtils.map(definition);
		}

		ReportRenderer renderer = new DefaultWebRenderer();
		RenderingMode mode = renderer.getRenderingModes(definition).iterator().next();

		ReportRequest request = new ReportRequest();
		request.setReportDefinition(mappedDefinition);
		request.setRenderingMode(mode);

		request = reportService.queueReport(request);
		reportService.processNextQueuedReports();

		log.debug("Requested report: " + reportId);

		return SimpleObject.fromObject(request, ui, "id");
	}
}