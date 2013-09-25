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

package org.openmrs.module.kenyaemr.reporting.builder.common;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyacore.report.IndicatorReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportManager;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.test.ReportingTestUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * Tests for {@link Moh731ReportBuilder}
 */
public class Moh731ReportBuilderTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private ReportManager reportManager;

	@Autowired
	private RegimenManager regimenManager;

	@Autowired
	private Moh731ReportBuilder reportBuilder;

	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-metadata.xml");
		executeDataSet("dataset/test-drugs.xml");

		regimenManager.refresh();
		reportManager.refresh();
	}

	@Test
	public void test() throws Exception {
		Program hivProgram = MetadataUtils.getProgram(HivMetadata._Program.HIV);

		// Enroll patient #6 in the HIV program
		TestUtils.enrollInProgram(Context.getPatientService().getPatient(6), hivProgram, TestUtils.date(2012, 1, 15), null);

		IndicatorReportDescriptor report = (IndicatorReportDescriptor) reportManager.getReportDescriptor("kenyaemr.common.report.moh731");
		ReportDefinition rd = reportBuilder.getDefinition(report);

		// Run report on all patients for Jan 2012
		EvaluationContext context = ReportingTestUtils.reportingContext(Arrays.asList(2, 6, 7, 8, 999), TestUtils.date(2012, 1, 1), TestUtils.date(2012, 1, 31));

		ReportData data = Context.getService(ReportDefinitionService.class).evaluate(rd, context);

		Assert.assertEquals(1, data.getDataSets().size());
		MapDataSet dataSet = (MapDataSet) data.getDataSets().get("MOH 731 DSD");
		Assert.assertNotNull(dataSet);

		Assert.assertEquals(1, ((IndicatorResult) dataSet.getColumnValue(1, "HV03-09")).getValue().intValue());
		Assert.assertEquals(1, ((IndicatorResult) dataSet.getColumnValue(1, "HV03-13")).getValue().intValue());

		ReportingTestUtils.printReport(data);
	}
}