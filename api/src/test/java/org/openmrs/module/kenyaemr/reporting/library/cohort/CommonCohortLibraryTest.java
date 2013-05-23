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

package org.openmrs.module.kenyaemr.reporting.library.cohort;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.test.ReportingTestUtils;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Tests for {@link CommonCohortLibrary}
 */
public class CommonCohortLibraryTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonCohortLibrary commonCohortLibrary;

	private EvaluationContext context;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() {
		List<Integer> cohort = Arrays.asList(2, 6, 7, 8, 999);
		context = ReportingTestUtils.reportingContext(cohort, TestUtils.date(2012, 1, 1), TestUtils.date(2012, 1, 31));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.library.cohort.CommonCohortLibrary#males()
	 */
	@Test
	public void males_shouldReturnAllMalePatients() throws Exception {
		CohortDefinition cd = commonCohortLibrary.males();
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);

		// #7 and #8 are female, #999 is voided
		ReportingTestUtils.assertCohortContainsAll(Arrays.asList(2, 6), evaluated);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.library.cohort.CommonCohortLibrary#females()
	 */
	@Test
	public void females_shouldReturnAllFemalePatients() throws Exception {
		CohortDefinition cd = commonCohortLibrary.females();
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);

		// #2 and #6 are male, #999 is voided
		ReportingTestUtils.assertCohortContainsAll(Arrays.asList(7, 8), evaluated);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.library.cohort.CommonCohortLibrary#agedAtMost(int)
	 */
	@Test
	public void ageAtMost_shouldReturnAllPatientsAgedAtMost() throws Exception {
		CohortDefinition cd = commonCohortLibrary.agedAtMost(35);
		context.addParameterValue("effectiveDate", context.getParameterValue("endDate"));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);

		// #6 will be 5, #7 will be 35, #8 has no birthdate, #999 is voided
		ReportingTestUtils.assertCohortContainsAll(Arrays.asList(6, 7), evaluated);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.library.cohort.CommonCohortLibrary#agedAtLeast(int)
	 */
	@Test
	public void ageAtLeast_shouldReturnAllPatientsAgedAtLeast() throws Exception {
		CohortDefinition cd = commonCohortLibrary.agedAtLeast(35);
		context.addParameterValue("effectiveDate", context.getParameterValue("endDate"));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);

		// #7 will be 35, #8 has no birthdate, #999 is voided
		ReportingTestUtils.assertCohortContainsAll(Arrays.asList(2, 7), evaluated);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.library.cohort.CommonCohortLibrary#agedAtLeast(int)
	 */
	@Test
	public void femalesAgedAtLeast18_shouldReturnAllPatientsAgedAtLeast() throws Exception {
		CohortDefinition cd = commonCohortLibrary.femalesAgedAtLeast18();
		context.addParameterValue("effectiveDate", context.getParameterValue("endDate"));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);

		// #7 will be 35, #8 has no birthdate, #999 is voided
		ReportingTestUtils.assertCohortContainsAll(Arrays.asList(7), evaluated);
	}
}