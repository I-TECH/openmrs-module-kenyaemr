/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.builder.common;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.test.ReportingTestUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;

/**
 * Test for {@link ArtDrugReportBuilder}
 */

public class ArtDrugReportBuilderTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private RegimenManager regimenManager;

	@Autowired
	ArtDrugReportBuilder builder;

	@Autowired
	CommonMetadata commonMetadata;

	@Autowired
	HivMetadata hivMetadata;

	@Autowired
	@Qualifier("kenyaemr.common.report.artDrug")
	private ReportDescriptor report;

	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-drugs.xml");

		commonMetadata.install();
		hivMetadata.install();
		regimenManager.refresh();

	}

	@Test
	public void test() throws Exception {
		ReportDefinition rd = builder.build(report);

		// Run report on all patients for June 2012
		EvaluationContext context = ReportingTestUtils.reportingContext(Arrays.asList(2, 6, 7, 8, 999), TestUtils.date(2012, 6, 1), TestUtils.date(2012, 6, 30));

		ReportData data = Context.getService(ReportDefinitionService.class).evaluate(rd, context);

		Assert.assertThat(data.getDataSets().size(), is(1));
		Assert.assertThat(data.getDataSets(), hasKey("ART Drugs"));

		// Check that report definition can be serialized/de-serialized
		Context.getService(ReportDefinitionService.class).saveDefinition(rd);
		Context.getService(ReportDefinitionService.class).getDefinition(rd.getId());

	}
}
