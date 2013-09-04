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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyacore.report.ReportBuilder;
import org.openmrs.module.kenyaemr.test.ReportingTestUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.reporting.builder.patientlist.DecliningCD4Report}
 */
public class DecliningCD4ReportTest extends BaseModuleContextSensitiveTest {

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
	}

	@Test
	public void testReport() throws Exception {
		Program hivProgram = MetadataUtils.getProgram(Metadata.Program.HIV);

		// Enroll patients #6, #7 and #8 in the HIV Program
		PatientService ps = Context.getPatientService();
		for (int i = 6; i <= 8; ++i) {
			TestUtils.enrollInProgram(ps.getPatient(i), hivProgram, new Date());
		}

		// Give patients #7 and #8 a CD4 count 180 days ago
		Concept cd4 = Context.getConceptService().getConcept(5497);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -180);
		TestUtils.saveObs(Context.getPatientService().getPatient(7), cd4, 123d, calendar.getTime());
		TestUtils.saveObs(Context.getPatientService().getPatient(8), cd4, 123d, calendar.getTime());

		// Give patient #7 a lower CD4 count today
		TestUtils.saveObs(Context.getPatientService().getPatient(7), cd4, 120d, new Date());

		// Give patient #8 a higher CD4 count today
		TestUtils.saveObs(Context.getPatientService().getPatient(8), cd4, 126d, new Date());

		Context.flushSession();

		ReportBuilder report = new DecliningCD4Report();
		ReportDefinition rd = report.getReportDefinition();
		EvaluationContext ec = new EvaluationContext();

		try {
			ReportData data = Context.getService(ReportDefinitionService.class).evaluate(rd, ec);

			ReportingTestUtils.checkPatientAlertListReport(Collections.singleton("1321200001"), "HIV Unique ID", data);
			ReportingTestUtils.printReport(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
