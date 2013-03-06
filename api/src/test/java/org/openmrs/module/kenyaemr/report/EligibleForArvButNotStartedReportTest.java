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
package org.openmrs.module.kenyaemr.report;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.report.patientlist.EligibleForArtReport;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class EligibleForArvButNotStartedReportTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");
	}
	
	@Test
	public void testReport() throws Exception {

		// Get HIV Program
		ProgramWorkflowService pws = Context.getProgramWorkflowService();
		Program hivProgram = pws.getProgramByUuid(MetadataConstants.HIV_PROGRAM_UUID);

		// Enroll patients #6 and #7 in the HIV Program
		PatientService ps = Context.getPatientService();
		for (int i = 6; i <= 7; ++i) {
			TestUtils.enrollInProgram(ps.getPatient(i), hivProgram, new Date());
		}

		ReportBuilder report = new EligibleForArtReport();
		ReportDefinition rd = report.getReportDefinition();
		
		EvaluationContext ec = new EvaluationContext();
		SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		ReportData data = Context.getService(ReportDefinitionService.class).evaluate(rd, ec);

		TestUtils.printReport(data);
	}
}
