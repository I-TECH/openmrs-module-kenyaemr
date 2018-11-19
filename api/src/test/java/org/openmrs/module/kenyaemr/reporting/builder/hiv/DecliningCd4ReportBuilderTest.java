/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
