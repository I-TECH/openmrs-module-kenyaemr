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

package org.openmrs.module.kenyaemr.reporting.library.shared.hiv;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyacore.test.ReportingTestUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.Fraction;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.indicator.CohortIndicatorResult;
import org.openmrs.module.reporting.indicator.Indicator;
import org.openmrs.module.reporting.indicator.service.IndicatorService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link QiIndicatorLibrary}
 */
public class QiIndicatorLibraryTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private QiIndicatorLibrary qiIndicatorLibrary;

	private EvaluationContext context;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		hivMetadata.install();

		List<Integer> cohort = Arrays.asList(2, 6, 7, 8, 999);
		context = ReportingTestUtils.reportingContext(cohort, TestUtils.date(2012, 6, 1), TestUtils.date(2012, 6, 30));
	}

	/**
	 * @see QiIndicatorLibrary#hivMonitoringCd4()
	 */
	@Test
	public void hivMonitoringCd4() throws Exception {
		Concept cd4Percent = Dictionary.getConcept(Dictionary.CD4_PERCENT);
		EncounterType hivConsultation = MetadataUtils.existing(EncounterType.class, HivMetadata._EncounterType.HIV_CONSULTATION);

		// Give patient #6 an HIV visit on June 1st
		TestUtils.saveEncounter(TestUtils.getPatient(6), hivConsultation, TestUtils.date(2012, 6, 1));

		// Give patient #7 an HIV visit on Jan 1st
		TestUtils.saveEncounter(TestUtils.getPatient(7), hivConsultation, TestUtils.date(2012, 1, 1));

		// Give patient #7 a CD4 percentage on Jun 1st
		TestUtils.saveObs(TestUtils.getPatient(7), cd4Percent, 30, TestUtils.date(2012, 6, 1));

		Indicator ind = qiIndicatorLibrary.hivMonitoringCd4();
		context.addParameterValue("endDate", TestUtils.date(2012, 6, 30));
		CohortIndicatorResult result = (CohortIndicatorResult) Context.getService(IndicatorService.class).evaluate(ind, context);
		Fraction fraction = (Fraction) CohortIndicatorResult.getResultValue(result);

		Assert.assertThat(fraction.getNumerator(), is(1));
		Assert.assertThat(fraction.getDenominator(), is(1));
	}
}