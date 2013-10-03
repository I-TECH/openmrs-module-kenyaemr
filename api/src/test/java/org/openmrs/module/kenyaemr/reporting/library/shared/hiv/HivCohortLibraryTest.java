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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.test.EmrTestUtils;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.test.ReportingTestUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivCohortLibrary}
 */
public class HivCohortLibraryTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private HivCohortLibrary hivCohortLibrary;

	private EvaluationContext context;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-drugs.xml");

		commonMetadata.install();
		hivMetadata.install();

		PatientService ps = Context.getPatientService();
		Concept azt = Context.getConceptService().getConcept(86663);
		Concept _3tc = Context.getConceptService().getConcept(78643);
		Concept efv = Context.getConceptService().getConcept(75523);

		// Put patient #6 on AZT + 3TC + EFV from June 1st to June 30th
		EmrTestUtils.saveRegimenOrder(ps.getPatient(6), Arrays.asList(azt, _3tc, efv), TestUtils.date(2012, 6, 1), TestUtils.date(2012, 6, 30));

		// Put patient #7 on AZT + 3TC + EFV from May 31st 2012 (also has a drug order starting 2008 in standardTestData.xml)
		EmrTestUtils.saveRegimenOrder(ps.getPatient(7), Arrays.asList(azt, _3tc, efv), TestUtils.date(2012, 5, 31), null);

		// Put patient #8 on AZT + 3TC + EFV from July 1st (out of calculation range)
		EmrTestUtils.saveRegimenOrder(ps.getPatient(8), Arrays.asList(azt, _3tc, efv), TestUtils.date(2012, 7, 1), null);

		List<Integer> cohort = Arrays.asList(2, 6, 7, 8, 999);
		context = ReportingTestUtils.reportingContext(cohort, TestUtils.date(2012, 6, 1), TestUtils.date(2012, 6, 30));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivCohortLibrary#startedArt()
	 */
	@Test
	public void startedArt_shouldReturnPatientsWhoStartedAfterDate() throws Exception {
		// Check with just start date
		CohortDefinition cd = hivCohortLibrary.startedArt();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivCohortLibrary#startedArt()
	 */
	@Test
	public void startedArt_shouldReturnPatientsWhoStartedBeforeDate() throws Exception {
		// Check with just end date
		CohortDefinition cd = hivCohortLibrary.startedArt();
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(2, 6, 7), evaluated); // Patient #2 has old orders in standardTestDataset.xml
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivCohortLibrary#startedArt()
	 */
	@Test
	public void startedArt_shouldReturnPatientsWhoStartedBetweenDates() throws Exception {
		// Check with both start and end date
		CohortDefinition cd = hivCohortLibrary.startedArt();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivCohortLibrary#enrolledExcludingTransfers()
	 */
	@Test
	public void enrolledExcludingTransfers() throws Exception {
		Program hivProgram = MetadataUtils.getProgram(HivMetadata._Program.HIV);
		Concept transferInDate = Dictionary.getConcept(Dictionary.TRANSFER_IN_DATE);

		// Enroll #6 on June 1st
		TestUtils.enrollInProgram(Context.getPatientService().getPatient(6), hivProgram, TestUtils.date(2012, 6, 1));

		// Enroll #7 on June 1st as a transfer in
		TestUtils.enrollInProgram(Context.getPatientService().getPatient(7), hivProgram, TestUtils.date(2012, 6, 1));
		TestUtils.saveObs(Context.getPatientService().getPatient(7), transferInDate, TestUtils.date(2012, 6, 1),  TestUtils.date(2012, 6, 1));

		// Enroll #8 on July 1st
		TestUtils.enrollInProgram(Context.getPatientService().getPatient(8), hivProgram, TestUtils.date(2012, 7, 1));

		CohortDefinition cd = hivCohortLibrary.enrolledExcludingTransfers();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.library.shared.hiv.HivCohortLibrary#referredFrom(org.openmrs.Concept...)
	 */
	@Test
	public void referredFrom_shouldReturnPatientsWithSpecifiedEntryPoint() throws Exception {
		EncounterType hivEnrollEncType = MetadataUtils.getEncounterType(HivMetadata._EncounterType.HIV_ENROLLMENT);
		Concept method = Dictionary.getConcept(Dictionary.METHOD_OF_ENROLLMENT);
		Concept pmtct = Dictionary.getConcept(Dictionary.PMTCT_PROGRAM);
		Concept other = Dictionary.getConcept(Dictionary.OTHER_NON_CODED);

		// Enroll #6 on June 15th from PMTCT
		Encounter enrollEnc = TestUtils.saveEncounter(Context.getPatientService().getPatient(6), hivEnrollEncType, TestUtils.date(2012, 6, 15));
		Obs entryObs = TestUtils.saveObs(Context.getPatientService().getPatient(6), method, pmtct, TestUtils.date(2012, 6, 15));
		enrollEnc.addObs(entryObs);
		Context.getEncounterService().saveEncounter(enrollEnc);

		// Enroll #7 on June 15th from OTHER
		enrollEnc = TestUtils.saveEncounter(Context.getPatientService().getPatient(7), hivEnrollEncType, TestUtils.date(2012, 6, 15));
		entryObs = TestUtils.saveObs(Context.getPatientService().getPatient(6), method, other, TestUtils.date(2012, 6, 15));
		enrollEnc.addObs(entryObs);
		Context.getEncounterService().saveEncounter(enrollEnc);

		// Enroll #8 on July 1st from PMTCT
		enrollEnc = TestUtils.saveEncounter(Context.getPatientService().getPatient(8), hivEnrollEncType, TestUtils.date(2012, 7, 1));
		entryObs = TestUtils.saveObs(Context.getPatientService().getPatient(8), method, pmtct, TestUtils.date(2012, 7, 1));
		enrollEnc.addObs(entryObs);
		Context.getEncounterService().saveEncounter(enrollEnc);

		Context.flushSession();

		CohortDefinition cd = hivCohortLibrary.referredFrom(pmtct);
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}
}