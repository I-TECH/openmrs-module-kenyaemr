
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
package org.openmrs.module.kenyaemr.reporting.library.shared.mchcs;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.test.ReportingTestUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.reporting.library.shared.mchcs.MchcsCohortLibrary}
 */

public class MchcsCohortLibraryTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private MchcsCohortLibrary mchcsCohortLibrary;

	private EvaluationContext context;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	/**
	 * @see MchcsCohortLibrary#pcrWithinMonths()
	 */
	@Test
	public void pcrWithinMonths_shouldReturnInfantPatientsWithPcrWithinMonths() throws Exception {
		Concept pcrTest = Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION);
		Concept detected = Dictionary.getConcept(Dictionary.DETECTED);
		Concept hivTest = Dictionary.getConcept(Dictionary.HIV_STATUS);
		Concept positive = Dictionary.getConcept(Dictionary.POSITIVE);
		Concept poorSampleQuality = Dictionary.getConcept(Dictionary.POOR_SAMPLE_QUALITY);

		//make patient #6 to have a pcr test with the results as detected
		TestUtils.saveObs(TestUtils.getPatient(6), pcrTest, detected, TestUtils.date(2013, 6, 10));
		//make patient #7 to have a pcr test with result poor sample
		TestUtils.saveObs(TestUtils.getPatient(7), pcrTest, poorSampleQuality, TestUtils.date(2013, 6, 15));
		//get the hiv status of #8 and positive
		TestUtils.saveObs(TestUtils.getPatient(8), hivTest, positive, TestUtils.date(2013, 6, 20));
		//only #6 and #7 should pass

		CohortDefinition cd =mchcsCohortLibrary.pcrWithinMonths();
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6, 7), evaluated);


	}

	/**
	 * @see MchcsCohortLibrary#pcrInitialTest()
	 */
	@Test
	public void pcrInitialTest_shouldReturnInfantPatientsWithPcrInitialTest() throws Exception {
		Concept contexualStatus = Dictionary.getConcept(Dictionary.TEXT_CONTEXT_STATUS);
		Concept initial = Dictionary.getConcept(Dictionary.TEST_STATUS_INITIAL);
		Concept complete = Dictionary.getConcept(Dictionary.CONFIRMATION_STATUS);
		//make patient #6 to have a pcr test with the results as detected
		TestUtils.saveObs(TestUtils.getPatient(6), contexualStatus, initial, TestUtils.date(2013, 6, 10));
		//make patient #7 to have a pcr test with result poor sample
		TestUtils.saveObs(TestUtils.getPatient(7), contexualStatus, complete, TestUtils.date(2013, 6, 15));
		//only #6 to qualify
		CohortDefinition cd =mchcsCohortLibrary.pcrInitialTest();
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}
}
