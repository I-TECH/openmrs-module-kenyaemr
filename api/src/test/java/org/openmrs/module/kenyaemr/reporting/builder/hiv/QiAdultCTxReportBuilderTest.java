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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyacore.test.ReportingTestUtils;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link QiAdultCTxReportBuilder}
 */
public class QiAdultCTxReportBuilderTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private TbMetadata tbMetadata;

	@Autowired
	private MchMetadata mchMetadata;

	@Autowired
	@Qualifier("kenyaemr.hiv.report.qi.adult.c.tx")
	private ReportDescriptor report;

	@Autowired
	private QiAdultCTxReportBuilder builder;

	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		hivMetadata.install();
		tbMetadata.install();
		mchMetadata.install();
	}

	@Test
	public void test() throws Exception {
		ReportDefinition rd = builder.build(report);

		// Run report on all patients for June 2012
		EvaluationContext context = ReportingTestUtils.reportingContext(Arrays.asList(2, 6, 7, 8, 999), TestUtils.date(2012, 6, 1), TestUtils.date(2012, 6, 30));

		ReportData data = Context.getService(ReportDefinitionService.class).evaluate(rd, context);

		//ReportingTestUtils.printReport(data);

		Assert.assertThat(data.getDataSets().size(), is(1));

		// Check that report definition can be serialized/de-serialized
		Context.getService(ReportDefinitionService.class).saveDefinition(rd);
		Context.getService(ReportDefinitionService.class).getDefinition(rd.getId());
	}
}