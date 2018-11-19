/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.converter.simplifier;

import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.simplifier.AbstractSimplifier;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts a report request to a simple object
 */
@Component
public class ReportRequestSimplifier extends AbstractSimplifier<ReportRequest> {

	@Autowired
	private UiUtils ui;

	@Autowired
	private KenyaUiUtils kenyaui;

	@Autowired
	private ReportDefinitionSimplifier definitionSimplifier;

	/**
	 * @see AbstractSimplifier#simplify(Object)
	 */
	@Override
	protected SimpleObject simplify(ReportRequest request) {
		ReportDefinition definition = request.getReportDefinition().getParameterizable();

		Long timeTaken = getTimeTaken(request);

		SimpleObject ret = new SimpleObject();
		ret.put("id", request.getId());
		ret.put("report", definitionSimplifier.convert(definition));
		ret.put("requestDate", kenyaui.formatDateParam(request.getRequestDate()));
		ret.put("requestedBy", ui.simplifyObject(request.getRequestedBy()));
		ret.put("status", request.getStatus());
		ret.put("finished", request.getStatus().equals(ReportRequest.Status.COMPLETED) || request.getStatus().equals(ReportRequest.Status.FAILED));
		ret.put("timeTaken", timeTaken != null ? kenyaui.formatDuration(timeTaken) : null);
		ret.put("parameters", getDate(request));
		ret.put("hasData", getReportData(request));
		ret.put("hasDataSet", getReportDataSet(request));
		return ret;
	}

	/**
	 * Calculates a time taken display value for the given request
	 * @param request the request
	 * @return the time taken in milliseconds
	 */
	protected Long getTimeTaken(ReportRequest request) {
		if (request.getEvaluateStartDatetime() == null || request.getStatus().equals(ReportRequest.Status.FAILED)) {
			return null;
		}

		Date start = request.getEvaluateStartDatetime();
		Date end = request.getEvaluateCompleteDatetime() != null ? request.getEvaluateCompleteDatetime() : new Date();

		return end.getTime() - start.getTime();
	}

	Map<String, Object> getDate(ReportRequest reportRequest) {
		Map<String, Object> mappings = new HashMap<String, Object>();

		if(reportRequest.getReportDefinition().getParameterMappings().isEmpty()){
			mappings.put("startDate", new Date());
		}
		else {
			mappings.putAll(reportRequest.getReportDefinition().getParameterMappings());
		}

		return mappings;
	}

	public boolean getReportData(ReportRequest request) {
		boolean hasData = false;
		ReportService reportService = Context.getService(ReportService.class);
		ReportData reportData = reportService.loadReportData(request);
		if(reportData != null){
			hasData = true;
		}

		return hasData;
	}

	public boolean getReportDataSet(ReportRequest request) {
		boolean hasDataSet = false;
		ReportService reportService = Context.getService(ReportService.class);
		ReportData reportData = reportService.loadReportData(request);
		if(reportData != null && reportData.getDataSets().size() > 0){
			for(Map.Entry<String, DataSet> dataSetEntry : reportData.getDataSets().entrySet()){
				if(dataSetEntry.getValue() != null){
					DataSet dataSetRows = dataSetEntry.getValue();
					if(dataSetRows.getMetaData().getColumnCount() > 0){
						hasDataSet = true;
					}
				}
			}

		}

		return hasDataSet;
	}
}