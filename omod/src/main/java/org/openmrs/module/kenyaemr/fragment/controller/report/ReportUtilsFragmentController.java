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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportManager;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.AppAction;
import org.openmrs.module.kenyaui.annotation.SharedAction;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
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
import org.openmrs.ui.framework.fragment.FragmentActionRequest;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AJAX utility methods for reports
 */
public class ReportUtilsFragmentController {

	protected static final Log log = LogFactory.getLog(ReportUtilsFragmentController.class);

	private static final DateFormat iso8601Formatter = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Requests a report evaluation
	 * @param reportUuid the report definition UUID
	 * @param reportManager the report manager
	 * @return the report request id
	 */
	@SharedAction
	public Object requestReport(@RequestParam("reportUuid") String reportUuid,
							    UiUtils ui,
							    @SpringBean KenyaUiUtils kenyaui,
							    @SpringBean FragmentActionRequest actionRequest,
							    @SpringBean ReportManager reportManager,
							    @SpringBean ReportService reportService,
							    @SpringBean ReportDefinitionService definitionService) throws ParseException {

		ReportDefinition definition = definitionService.getDefinitionByUuid(reportUuid);
		ReportDescriptor report = reportManager.getReportDescriptor(definition);

		CoreUtils.checkAccess(report, kenyaui.getCurrentApp(actionRequest));

		Collection<String> missingParameters = new ArrayList<String>();
		Map<String, Object> parameterValues = new HashMap<String, Object>();

		// Match incoming parameters in the request to report parameters
		for (Parameter parameter : definition.getParameters()) {
			String submitted = actionRequest.getParameter("param[" + parameter.getName() + "]");

			Object converted = StringUtils.isNotEmpty(submitted) ? ui.convert(submitted, parameter.getType()) : parameter.getDefaultValue();

			if (converted == null) {
				missingParameters.add(parameter.getName());
			}

			parameterValues.put(parameter.getName(), converted);
		}

		if (missingParameters.size() > 0) {
			return new FailureResult("Missing report parameters");
		}


		Mapped<ReportDefinition> mappedDefinition = new Mapped<ReportDefinition>(definition, parameterValues);

		ReportRenderer renderer = new DefaultWebRenderer();
		RenderingMode mode = renderer.getRenderingModes(definition).iterator().next();

		ReportRequest request = new ReportRequest();
		request.setReportDefinition(mappedDefinition);
		request.setRenderingMode(mode);

		request = reportService.queueReport(request);
		reportService.processNextQueuedReports();

		log.info("Requested report '" + definition.getName() + "' with params " + parameterValues);

		return SimpleObject.fromObject(request, ui, "id");
	}

	/**
	 * Cancels the given report request
	 * @param request the report request
	 * @param ui the UI utils
	 * @return the result
	 */
	@AppAction(EmrConstants.APP_ADMIN)
	public Object cancelRequest(@RequestParam("requestId") ReportRequest request,
									  UiUtils ui,
									  @SpringBean ReportService reportService) {

		boolean cancelable = ReportRequest.Status.REQUESTED.equals(request.getStatus())
				|| ReportRequest.Status.PROCESSING.equals(request.getStatus());

		if (cancelable) {
			reportService.purgeReportRequest(request);
			return new SuccessResult(ui.message("Report request cancelled"));
		}

		return new FailureResult(ui.message("Report request could not be cancelled"));
	}

	/**
	 * Gets the finished (failed or completed) requests for the given report
	 * @param reportUuid the report definition UUID
	 * @param ui the UI utils
	 * @param reportService the report service
	 * @return the simplified requests
	 */
	public SimpleObject[] getFinishedRequests(@RequestParam(value = "reportUuid", required = false) String reportUuid,
									  UiUtils ui,
									  @SpringBean ReportService reportService) {

		List<ReportRequest> requests = fetchRequests(reportUuid, true, reportService);

		return ui.simplifyCollection(requests);
	}

	/**
	 * Gets the queued requests for the given report
	 * @param reportUuid the report definition UUID
	 * @param ui the UI utils
	 * @param reportService the report service
	 * @return the simplified requests
	 */
	public SimpleObject[] getQueuedRequests(@RequestParam(value = "reportUuid", required = false) String reportUuid,
											   UiUtils ui,
											   @SpringBean ReportService reportService) {

		List<ReportRequest> requests = fetchRequests(reportUuid, false, reportService);

		// Filter out finished requests
		CollectionUtils.filter(requests, new Predicate() {
			@Override
			public boolean evaluate(Object obj) {
				ReportRequest request = (ReportRequest) obj;
				return !(ReportRequest.Status.COMPLETED.equals(request.getStatus()) || ReportRequest.Status.FAILED.equals(request.getStatus()));
			}
		});

		return ui.simplifyCollection(requests);
	}

	/**
	 * Helper method to fetch report requests
	 * @param reportUuid the report definition UUID (optional)
	 * @param finishedOnly only finished requests (completed or failed)
	 * @param reportService the report service
	 * @return the report requests
	 */
	protected List<ReportRequest> fetchRequests(String reportUuid, boolean finishedOnly, ReportService reportService) {
		ReportDefinition definition = null;

		// Hack to avoid loading (and thus de-serialising) the entire report
		if (StringUtils.isNotEmpty(reportUuid)) {
			definition = new ReportDefinition();
			definition.setUuid(reportUuid);
		}

		List<ReportRequest> requests = (finishedOnly)
				? reportService.getReportRequests(definition, null, null, ReportRequest.Status.COMPLETED, ReportRequest.Status.FAILED)
				: reportService.getReportRequests(definition, null, null);

		// Sort by requested date desc (more sane than the default sorting)
		Collections.sort(requests, new Comparator<ReportRequest>() {
			@Override
			public int compare(ReportRequest request1, ReportRequest request2) {
				return OpenmrsUtil.compareWithNullAsEarliest(request2.getRequestDate(), request1.getRequestDate());
			}
		});

		return requests;
	}
}