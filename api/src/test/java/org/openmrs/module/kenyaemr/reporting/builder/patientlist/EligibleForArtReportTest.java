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

package org.openmrs.module.kenyaemr.reporting.builder.patientlist;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyautil.MetadataUtils;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyacore.reporting.ReportBuilder;
import org.openmrs.module.kenyaemr.test.ReportingTestUtils;
import org.openmrs.module.kenyautil.test.TestUtils;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.reporting.builder.patientlist.EligibleForArtReport}
 */
public class EligibleForArtReportTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");
	}
	
	@Test
	public void testReport() throws Exception {
		Program hivProgram = MetadataUtils.getProgram(Metadata.HIV_PROGRAM);

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

		ReportingTestUtils.printReport(data);
	}
}
