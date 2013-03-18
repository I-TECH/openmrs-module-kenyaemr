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

import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class Moh731ReportTest extends BaseModuleContextSensitiveTest {

	@Autowired
	KenyaEmr emr;

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");

		emr.contextRefreshed();
	}

	@Test
	public void test() throws Exception {
		Moh731Report report = new Moh731Report();
		ReportDefinition rd = report.getReportDefinition();
		
		EvaluationContext ec = new EvaluationContext();
		SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		ec.addParameterValue("startDate", ymd.parse("2012-07-01"));
		ec.addParameterValue("endDate", ymd.parse("2012-07-31"));
		ReportData data = Context.getService(ReportDefinitionService.class).evaluate(rd, ec);

		//TestUtils.printReport(data);
	}
}