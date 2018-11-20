/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.test.EmrTestUtils;
import org.openmrs.module.kenyacore.test.ReportingTestUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link ArtCohortLibrary}
 */
@Ignore
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
		EncounterType scheduled = Context.getEncounterService().getEncounterType(1);
		Encounter scheduledEncounter1 = new Encounter();
		scheduledEncounter1.setPatient(TestUtils.getPatient(6));
		scheduledEncounter1.setEncounterType(scheduled);
		scheduledEncounter1.setEncounterDatetime(TestUtils.date(2012, 6, 1));
		scheduledEncounter1 = Context.getEncounterService().saveEncounter(scheduledEncounter1);

		Encounter scheduledEncounter2 = new Encounter();
		scheduledEncounter2.setPatient(TestUtils.getPatient(7));
		scheduledEncounter2.setEncounterType(scheduled);
		scheduledEncounter2.setEncounterDatetime(TestUtils.date(2012, 5, 31));
		scheduledEncounter2 = Context.getEncounterService().saveEncounter(scheduledEncounter2);

		Encounter scheduledEncounter3 = new Encounter();
		scheduledEncounter3.setPatient(TestUtils.getPatient(8));
		scheduledEncounter3.setEncounterType(scheduled);
		scheduledEncounter3.setEncounterDatetime(TestUtils.date(2012, 7, 1));
		scheduledEncounter3 = Context.getEncounterService().saveEncounter(scheduledEncounter3);

		EmrTestUtils.saveRegimenOrder(TestUtils.getPatient(6), Arrays.asList(azt, _3tc, efv), TestUtils.date(2012, 6, 1), null, scheduledEncounter1);

		// Put patient #7 on AZT + 3TC + EFV from May 31st 2012 (also has a drug order starting 2008 in standardTestData.xml)
		EmrTestUtils.saveRegimenOrder(TestUtils.getPatient(7), Arrays.asList(azt, _3tc, efv), TestUtils.date(2012, 5, 31), null, scheduledEncounter2);

		// Put patient #8 on AZT + 3TC + EFV from July 1st (out of calculation range)
		EmrTestUtils.saveRegimenOrder(TestUtils.getPatient(8), Arrays.asList(azt, _3tc, efv), TestUtils.date(2012, 7, 1), null, scheduledEncounter3);

		List<Integer> cohort = Arrays.asList(2, 6, 7, 8, 999);
		context = ReportingTestUtils.reportingContext(cohort, TestUtils.date(2012, 6, 1), TestUtils.date(2012, 6, 30));
	}

	/**
	 * @see ArtCohortLibrary#onRegimen(java.util.List)
	 */
	@Test
	public void onRegimen_shouldReturnPatientsOnGivenRegimen() throws Exception {
		EncounterType scheduled = Context.getEncounterService().getEncounterType("Scheduled");

		// Give patient #6 a scheduled encounter before 3 months of report
		TestUtils.saveEncounter(TestUtils.getPatient(6), scheduled, TestUtils.date(2012, 1, 1));

		// Give patient #7 a scheduled encounter within 3 months of report
		TestUtils.saveEncounter(TestUtils.getPatient(7), scheduled, TestUtils.date(2012, 6, 1));

		CohortDefinition cd = artCohortLibrary.onRegimen(Arrays.asList(azt, _3tc, efv));
		context.addParameterValue("onDate", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);
	}

	/**
	 * @see ArtCohortLibrary#startedArt()
	 */
	@Test
	public void startedArt_shouldReturnPatientsWhoStartedAfterDate() throws Exception {
		// Check with just start date
		CohortDefinition cd = artCohortLibrary.startedArt();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}

	/**
	 * @see ArtCohortLibrary#startedArt()
	 */
	@Test
	public void startedArt_shouldReturnPatientsWhoStartedBeforeDate() throws Exception {
		// Check with just end date
		CohortDefinition cd = artCohortLibrary.startedArt();
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(2, 6, 7), evaluated); // Patient #2 has old orders in standardTestDataset.xml
	}

	/**
	 * @see ArtCohortLibrary#startedArt()
	 */
	@Test
	public void startedArt_shouldReturnPatientsWhoStartedBetweenDates() throws Exception {
		// Check with both start and end date
		CohortDefinition cd = artCohortLibrary.startedArt();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}

	/**
	 * @see ArtCohortLibrary#netCohortMonths(int)
	 */
	@Test
	public void netCohortMonths_shouldReturnPatientsInNetCohortMonths() throws Exception {
		int months = 12;

		Concept dapsone = Dictionary.getConcept(Dictionary.DAPSONE);
		Concept azt = Dictionary.getConcept(Dictionary.ZIDOVUDINE);
		Concept _3tc = Dictionary.getConcept(Dictionary.LAMIVUDINE);
		Concept efv = Dictionary.getConcept(Dictionary.EFAVIRENZ);

		// Put patient #7 on Dapsone
		TestUtils.saveDrugOrder(TestUtils.getPatient(7), dapsone, TestUtils.date(2013, 1, 1), null);

		// Put patient #6 on AZT + 3TC + EFV from 1st jan to 31st jan
		EmrTestUtils.saveRegimenOrder(TestUtils.getPatient(6), Arrays.asList(azt, _3tc, efv), TestUtils.date(2012, 1, 1), TestUtils.date(2013, 1, 1));

		CohortDefinition cd = artCohortLibrary.netCohortMonths(months);
		context.addParameterValue("onDate", TestUtils.date(2013, 1, 1));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}
}