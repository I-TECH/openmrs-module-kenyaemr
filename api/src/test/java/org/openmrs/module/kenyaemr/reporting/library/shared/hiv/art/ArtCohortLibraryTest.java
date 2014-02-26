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

package org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.test.EmrTestUtils;
import org.openmrs.module.kenyaemr.test.ReportingTestUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Tests for {@link ArtCohortLibrary}
 */
public class ArtCohortLibraryTest extends BaseModuleContextSensitiveTest {

	private EvaluationContext context;

	@Autowired
	private ArtCohortLibrary artCohortLibrary;

	@Autowired
	private RegimenManager regimenManager;

	private Concept azt, _3tc, efv;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-drugs.xml");

		regimenManager.refresh();

		azt = Dictionary.getConcept(Dictionary.ZIDOVUDINE);
		_3tc = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		efv = Dictionary.getConcept(Dictionary.EFAVIRENZ);

		// Put patient #6 on AZT + 3TC + EFV from June 1st to June 30th
		EmrTestUtils.saveRegimenOrder(TestUtils.getPatient(6), Arrays.asList(azt, _3tc, efv), TestUtils.date(2012, 6, 1), TestUtils.date(2012, 6, 30));

		// Put patient #7 on AZT + 3TC + EFV from July 1st to July 31st
		EmrTestUtils.saveRegimenOrder(TestUtils.getPatient(7), Arrays.asList(azt, _3tc, efv), TestUtils.date(2012, 7, 1), TestUtils.date(2012, 7, 31));

		List<Integer> cohort = Arrays.asList(2, 6, 7, 8, 999);
		context = ReportingTestUtils.reportingContext(cohort, TestUtils.date(2012, 6, 1), TestUtils.date(2012, 6, 30));
	}

	/**
	 * @see ArtCohortLibrary#onRegimen(java.util.List)
	 */
	@Test
	public void onRegimen_shouldReturnPatientsOnGivenRegimen() throws Exception {
		CohortDefinition cd = artCohortLibrary.onRegimen(Arrays.asList(azt, _3tc, efv));
		Date endDate = TestUtils.date(2012,9,1);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);

		// Give patient #7 a scheduled encounter 100 days ago
		calendar.add(Calendar.DATE, -100);
		EncounterType scheduledEncType = Context.getEncounterService().getEncounterType("Scheduled");
		TestUtils.saveEncounter(TestUtils.getPatient(7), scheduledEncType, calendar.getTime());

		// Give patient #6 a scheduled encounter 50 days ago
		calendar.add(Calendar.DATE, -50);
		TestUtils.saveEncounter(TestUtils.getPatient(6), scheduledEncType, calendar.getTime());

		context.addParameterValue("onDate", TestUtils.date(2012, 6, 15));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}
}