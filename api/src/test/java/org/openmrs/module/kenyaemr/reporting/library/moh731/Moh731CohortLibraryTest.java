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

package org.openmrs.module.kenyaemr.reporting.library.moh731;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.test.ReportingTestUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link Moh731CohortLibrary}
 */
public class Moh731CohortLibraryTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private Moh731CohortLibrary moh731Cohorts;

	private EvaluationContext context;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();

		List<Integer> cohort = Arrays.asList(2, 6, 7, 8, 999);
		context = ReportingTestUtils.reportingContext(cohort, TestUtils.date(2012, 6, 1), TestUtils.date(2012, 6, 30));
	}

	/**
	 * @see Moh731CohortLibrary#revisitsArt()
	 */
	@Test
	public void revisitsArt() throws Exception {
		EncounterType triage = MetadataUtils.getEncounterType(CommonMetadata._EncounterType.TRIAGE);
		Concept stavudine = Context.getConceptService().getConcept(84309);

		// Start patient #6 this month and give them a visit in the reporting period + 2 months
		TestUtils.saveDrugOrder(TestUtils.getPatient(6), stavudine, TestUtils.date(2012, 6, 10), null);
		TestUtils.saveEncounter(TestUtils.getPatient(6), triage, TestUtils.date(2012, 6, 20));

		// Start patient #7 in previous month and give them a visit in the reporting period + 2 months
		TestUtils.saveDrugOrder(TestUtils.getPatient(7), stavudine, TestUtils.date(2012, 5, 10), null);
		TestUtils.saveEncounter(TestUtils.getPatient(7), triage, TestUtils.date(2012, 6, 20));

		// Start patient #8 and give them visit outside of reporting period + 2 month window
		TestUtils.saveDrugOrder(TestUtils.getPatient(8), stavudine, TestUtils.date(2012, 1, 10), null);
		TestUtils.saveEncounter(TestUtils.getPatient(8), triage, TestUtils.date(2012, 1, 20));

		CohortDefinition cd = moh731Cohorts.revisitsArt();
		context.addParameterValue("fromDate", TestUtils.date(2012, 6, 1));
		context.addParameterValue("toDate", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);
	}
}