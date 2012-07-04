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

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.AppUiUtil;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.report.IndicatorReportManager;
import org.openmrs.module.reporting.common.ContentType;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.ui.framework.page.FileDownload;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.session.Session;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 */
public class ReportsRunMonthlyIndicatorReportPageController {
	
	public Object controller(Session session,
	                       PageModel model,
	                       @RequestParam("manager") String managerClassname,
	                       @RequestParam(required = false, value = "mode") String mode,
	                       @RequestParam(required = false, value = "startDate") Date startDate) throws Exception {
		
		AppUiUtil.startApp("kenyaemr.reports", session);
		
		IndicatorReportManager manager = Context.getService(KenyaEmrService.class).getReportManager(managerClassname);
		ReportDefinition rd = manager.getReportDefinition();
		
		model.addAttribute("definition", rd);
		
		if (startDate != null) {
			// generate the report
			EvaluationContext ec = new EvaluationContext();
			ec.addParameterValue("startDate", startDate);
			ec.addParameterValue("endDate", DateUtil.getEndOfMonth(startDate));
			ReportData data = Context.getService(ReportDefinitionService.class).evaluate(rd, ec);
			
			if ("excel".equals(mode)) {
				byte[] excelTemplate = manager.getExcelTemplate();
				if (excelTemplate == null) {
					throw new RuntimeException(managerClassname + " does not support Excel output");
				}
				
				ExcelTemplateRenderer renderer;
				{
					// this is a bit of a hack, copied from ExcelRendererTest in the reporting module, to avoid
					// needing to save the template and report design in the database
					ReportDesignResource resource = new ReportDesignResource();
					resource.setName("template.xls");
					resource.setContents(excelTemplate);
					
					final ReportDesign design = new ReportDesign();
					design.setName(rd.getName() + " design");
					design.setReportDefinition(rd);
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
				return new FileDownload(manager.getExcelFilename(ec), ContentType.EXCEL.getContentType(), out.toByteArray());
				
			} else {
				model.addAttribute("data", data);
			}
			
		} else {
			// show form with date options
			model.addAttribute("data", null);
			Map<String, String> startDateOptions = new LinkedHashMap<String, String>();
			SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat pretty = new SimpleDateFormat("MMMM yyyy");
			Date d = DateUtil.getStartOfMonth(new Date());
			for (int i = 0; i < 6; ++i) {
				d = DateUtil.getStartOfMonth(d, -1);
				startDateOptions.put(ymd.format(d), pretty.format(d));
			}
			model.addAttribute("startDateOptions", startDateOptions);
		}
		return null;
	}
	
}
