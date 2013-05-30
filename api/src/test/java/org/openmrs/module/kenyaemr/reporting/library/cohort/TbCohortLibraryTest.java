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
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.test.ReportingTestUtils;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link TbCohortLibrary}
 */
public class TbCohortLibraryTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private TbCohortLibrary tbCohortLibrary;

	private EvaluationContext context;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");

		EncounterType tbScreeningEncType = Metadata.getEncounterType(Metadata.TB_SCREENING_ENCOUNTER_TYPE);

		// Screen patient #2 on May 31st
		TestUtils.saveEncounter(Context.getPatientService().getPatient(2), tbScreeningEncType, TestUtils.date(2012, 5, 31));

		// Screen patient #6 on June 1st
		TestUtils.saveEncounter(Context.getPatientService().getPatient(6), tbScreeningEncType, TestUtils.date(2012, 6, 1));

		// Screen patient #7 on June 30th
		TestUtils.saveEncounter(Context.getPatientService().getPatient(7), tbScreeningEncType, TestUtils.date(2012, 6, 30));

		// Screen patient #8 on July 1st
		TestUtils.saveEncounter(Context.getPatientService().getPatient(8), tbScreeningEncType, TestUtils.date(2012, 7, 1));

		List<Integer> cohort = Arrays.asList(2, 6, 7, 8, 999);
		context = ReportingTestUtils.reportingContext(cohort, TestUtils.date(2012, 6, 1), TestUtils.date(2012, 6, 30));
	}

	/**
	 * @see TbCohortLibrary#screenedForTb()
	 */
	@Test
	public void screenedForTb_shouldReturnPatientsScreenedAfterDate() throws Exception {
		// Check with just start date
		CohortDefinition cd = tbCohortLibrary.screenedForTb();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6, 7, 8), evaluated);
	}

	/**
	 * @see TbCohortLibrary#screenedForTb()
	 */
	@Test
	public void screenedForTb_shouldReturnPatientsScreenedBeforeDate() throws Exception {
		// Check with just end date
		CohortDefinition cd = tbCohortLibrary.screenedForTb();
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(2, 6, 7), evaluated);
	}

	/**
	 * @see TbCohortLibrary#screenedForTb()
	 */
	@Test
	public void screenedForTb_shouldReturnPatientsScreenedBetweenDates() throws Exception {
		// Check with both start and end date
		CohortDefinition cd = tbCohortLibrary.screenedForTb();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6, 7), evaluated);
	}
}