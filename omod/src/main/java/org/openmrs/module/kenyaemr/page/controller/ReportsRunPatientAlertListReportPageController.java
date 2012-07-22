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

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.AppUiUtil;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.report.ReportManager;
import org.openmrs.module.reporting.common.ContentType;
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
public class ReportsRunPatientAlertListReportPageController {
	
	public Object controller(Session session,
	                       PageModel model,
	                       @RequestParam("manager") String managerClassname,
	                       @RequestParam(required = false, value = "mode") String mode) throws Exception {
		
		AppUiUtil.startApp("kenyaemr.reports", session);
		
		ReportManager manager = Context.getService(KenyaEmrService.class).getReportManager(managerClassname);
		ReportDefinition rd = manager.getReportDefinition();
		
		model.addAttribute("manager", manager);
		model.addAttribute("supportsExcel", StringUtils.isNotBlank(manager.getExcelFilename(new EvaluationContext())));
		model.addAttribute("definition", rd);
		
		// generate the report
		EvaluationContext ec = new EvaluationContext();
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
		
		return null;
	}
	
}
