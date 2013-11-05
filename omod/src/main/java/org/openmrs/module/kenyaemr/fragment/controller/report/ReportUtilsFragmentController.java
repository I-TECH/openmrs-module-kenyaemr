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
import org.openmrs.module.kenyacore.report.IndicatorReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportManager;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyaemr.util.EmrUiUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reporting.web.renderers.DefaultWebRenderer;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

/**
 * AJAX utility methods for reports
 */
public class ReportUtilsFragmentController {

	protected static final Log log = LogFactory.getLog(ReportUtilsFragmentController.class);

	/**
	 * Requests a report evaluation
	 * @param reportUuid the report definition UUID
	 * @param date the date (optional)
	 * @param reportManager the report manager
	 * @return the report request id
	 */
	public SimpleObject requestReport(@RequestParam("reportUuid") String reportUuid,
									  @RequestParam(required = false, value = "date") Date date,
									  UiUtils ui,
									  @SpringBean EmrUiUtils emrUi,
									  @SpringBean ReportManager reportManager,
									  @SpringBean ReportService reportService,
									  @SpringBean ReportDefinitionService definitionService) {

		ReportDefinition definition = definitionService.getDefinitionByUuid(reportUuid);
		ReportDescriptor report = reportManager.getReportDescriptor(definition);

		// TODO emrUi.checkReportAccess(pageRequest, report);

		Mapped<ReportDefinition> mappedDefinition;

		if (report instanceof IndicatorReportDescriptor) { // Indicator reports are always period reports
			Date startDate = date;
			Date endDate = DateUtil.getEndOfMonth(startDate);
			mappedDefinition = ReportUtils.map(definition, "startDate", startDate, "endDate", endDate);
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

		log.debug("Requested report: " + report.getTargetUuid());

		return SimpleObject.fromObject(request, ui, "id");
	}

	/**
	 * Gets the existing requests for the given report
	 * @param reportUuid the report definition UUID
	 * @param ui the UI utils
	 * @param reportService the report service
	 * @return the simplified requests
	 */
	public SimpleObject[] getRequests(@RequestParam("reportUuid") String reportUuid,
									  UiUtils ui,
									  @SpringBean ReportService reportService) {

		// Hack to avoid loading (and thus de-serialising) the entire report
		ReportDefinition definition = new ReportDefinition();
		definition.setUuid(reportUuid);

		List<ReportRequest> requests = reportService.getReportRequests(definition, null, null, null);

		return ui.simplifyCollection(requests);
	}
}