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

package org.openmrs.module.kenyaemr.reporting.builder.hiv;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyacore.test.ReportingTestUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Tests for {@link DecliningCd4ReportBuilder}
 */
public class DecliningCd4ReportBuilderTest extends BaseModuleContextSensitiveTest {

	@Autowired
	@Qualifier("kenyaemr.hiv.report.decliningCd4")
	private ReportDescriptor report;

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private DecliningCd4ReportBuilder reportBuilder;

	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		hivMetadata.install();
	}

	@Test
	public void testReport() throws Exception {
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		// Enrol patient #7 in the HIV program
		TestUtils.enrollInProgram(TestUtils.getPatient(7), hivProgram, new Date());

		// Give patient #7 a CD4 count 180 days ago
		Concept cd4 = Dictionary.getConcept(Dictionary.CD4_COUNT);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -180);
		TestUtils.saveObs(TestUtils.getPatient(7), cd4, 123d, calendar.getTime());

		// Give patient #7 a lower CD4 now
		TestUtils.saveObs(TestUtils.getPatient(7), cd4, 120d, new Date());

		Context.flushSession();

		// Evaluate report
		ReportDefinition rd = reportBuilder.build(report);
		EvaluationContext ec = new EvaluationContext();

		ReportData data = Context.getService(ReportDefinitionService.class).evaluate(rd, ec);

		ReportingTestUtils.checkStandardCohortReport(Collections.singleton(7), data);
		//ReportingTestUtils.printReport(data);
	}
}
