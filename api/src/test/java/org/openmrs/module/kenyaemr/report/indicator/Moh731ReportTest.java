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
package org.openmrs.module.kenyaemr.report.indicator;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.report.indicator.Moh731Report;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.TestUtil;

/**
 *
 */
@SkipBaseSetup
@Ignore
public class Moh731ReportTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
	public Boolean useInMemoryDatabase() {
	    return false;
	}
	
	/**
	 * @see org.openmrs.test.BaseContextSensitiveTest#getWebappName()
	 */
	@Override
	public String getWebappName() {
	    return "openmrs19";
	}
	
	@Test
	public void testRunMoh731Report() throws Exception {
		authenticate();
		
		Moh731Report report = new Moh731Report();
		ReportDefinition rd = report.getReportDefinition();
		
		EvaluationContext ec = new EvaluationContext();
		SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		ec.addParameterValue("startDate", ymd.parse("2012-07-01"));
		ec.addParameterValue("endDate", ymd.parse("2012-07-31"));
		ReportData data = Context.getService(ReportDefinitionService.class).evaluate(rd, ec);

		TestUtils.printReport(data);
		
		byte[] excelTemplate = report.loadExcelTemplate();
		
		ReportDesignResource resource = new ReportDesignResource();
		resource.setName("template.xls");
		resource.setContents(excelTemplate);
		
		final ReportDesign design = new ReportDesign();
		design.setName(rd.getName() + " design");
		design.setReportDefinition(rd);
		design.setRendererType(ExcelTemplateRenderer.class);
		design.addResource(resource);
		
		ExcelTemplateRenderer renderer = new ExcelTemplateRenderer() {
			public ReportDesign getDesign(String argument) {
				return design;
			}
		};
		
		FileOutputStream fos = new FileOutputStream("/tmp/test.xls"); // You will need to change this if you have no /tmp directory
		renderer.render(data, "xxx:xls", fos);
		IOUtils.closeQuietly(fos);
	}
}