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

package org.openmrs.module.kenyaemr.reporting.builder.indicator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.test.ReportingTestUtils;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * Tests for {@link Moh711Report}
 */
public class Moh711ReportTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private Moh711Report report;

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");
	}

	@Test
	public void test() throws Exception {
		Program hivProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.HIV_PROGRAM_UUID);

		// Enroll patient #6 in the HIV program
		TestUtils.enrollInProgram(Context.getPatientService().getPatient(6), hivProgram, TestUtils.date(2012, 1, 15), null);

		ReportDefinition rd = report.getReportDefinition();

		// Run report on all patients for Jan 2012
		EvaluationContext context = ReportingTestUtils.reportingContext(Arrays.asList(2, 6, 7, 8, 999), TestUtils.date(2012, 1, 1), TestUtils.date(2012, 1, 31));

		ReportData data = Context.getService(ReportDefinitionService.class).evaluate(rd, context);

		ReportingTestUtils.printReport(data);
	}
}