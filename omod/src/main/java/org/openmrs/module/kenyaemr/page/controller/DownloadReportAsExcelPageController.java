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

import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportManager;
import org.openmrs.module.kenyaemr.reporting.BaseIndicatorReport;
import org.openmrs.module.kenyaemr.reporting.EmrReportingUtils;
import org.openmrs.module.kenyaemr.reporting.ReportBuilder;
import org.openmrs.module.kenyaemr.util.EmrUiUtils;
import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.openmrs.module.reporting.common.ContentType;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.FileDownload;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Download report as Excel page
 */
@SharedPage
public class DownloadReportAsExcelPageController {
	
	public FileDownload controller(@RequestParam("reportId") String reportId,
								   @RequestParam(required = false, value = "startDate") Date startDate,
								   PageRequest pageRequest,
								   @SpringBean ReportManager reportManager,
								   @SpringBean EmrUiUtils emrUi) throws Exception {

		ReportDescriptor report = reportManager.getReportDescriptor(reportId);
		emrUi.checkReportAccess(pageRequest, report);

		ReportBuilder builder = EmrReportingUtils.getReportBuilder(report);

		if (!builder.isExcelRenderable()) {
			throw new RuntimeException(builder.getClass() + " does not support Excel rendering");
		}

		ReportDefinition definition = builder.getDefinition();

		// Evaluate the report
		EvaluationContext ec = new EvaluationContext();
		ec.addParameterValue("startDate", startDate);
		ec.addParameterValue("endDate", DateUtil.getEndOfMonth(startDate));
		ReportData data = Context.getService(ReportDefinitionService.class).evaluate(definition, ec);

		if (builder instanceof BaseIndicatorReport) {
			return renderAsExcel((BaseIndicatorReport) builder, definition, data);
		}
		else {
			throw new RuntimeException("Only indicator reports can currently be rendered as Excel");
		}
	}

	/**
	 * Renders an indicator report as Excel
	 * @param builder the indicator report builder
	 * @param definition the report definition
	 * @param data the evaluated report data
	 * @return the Excel file as a download
	 * @throws IOException
	 */
	protected FileDownload renderAsExcel(BaseIndicatorReport builder, ReportDefinition definition, ReportData data) throws IOException {
		byte[] excelTemplate = builder.loadExcelTemplate();

		ExcelTemplateRenderer renderer;
		{
			// this is a bit of a hack, copied from ExcelRendererTest in the reporting module, to avoid
			// needing to save the template and report design in the database
			ReportDesignResource resource = new ReportDesignResource();
			resource.setName("template.xls");
			resource.setContents(excelTemplate);

			final ReportDesign design = new ReportDesign();
			design.setName(definition.getName() + " design");
			design.setReportDefinition(definition);
			design.setRendererType(ExcelTemplateRenderer.class);
			design.addResource(resource);

			renderer = new ExcelTemplateRenderer() {
				public ReportDesign getDesign(String argument) {
					return design;
				}
			};
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		renderer.render(data, "xxx:xls", out);
		return new FileDownload(builder.getExcelDownloadFilename(data.getContext()), ContentType.EXCEL.getContentType(), out.toByteArray());
	}
}