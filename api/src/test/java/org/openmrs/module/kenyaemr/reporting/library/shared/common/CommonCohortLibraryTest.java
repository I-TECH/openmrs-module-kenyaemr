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

package org.openmrs.module.kenyaemr.reporting.library.shared.common;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyacore.test.ReportingTestUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.reporting.library.shared.common.CommonCohortLibrary}
 */
public class CommonCohortLibraryTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private CommonCohortLibrary commonCohortLibrary;

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
	 * @see CommonCohortLibrary#males()
	 */
	@Test
	public void males_shouldReturnAllMalePatients() throws Exception {
		CohortDefinition cd = commonCohortLibrary.males();
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);

		// #7 and #8 are female, #999 is voided
		ReportingTestUtils.assertCohortEquals(Arrays.asList(2, 6), evaluated);
	}

	/**
	 * @see CommonCohortLibrary#females()
	 */
	@Test
	public void females_shouldReturnAllFemalePatients() throws Exception {
		CohortDefinition cd = commonCohortLibrary.females();
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);

		// #2 and #6 are male, #999 is voided
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7, 8), evaluated);
	}

	/**
	 * @see CommonCohortLibrary#agedAtMost(int)
	 */
	@Test
	public void ageAtMost_shouldReturnAllPatientsAgedAtMost() throws Exception {
		CohortDefinition cd = commonCohortLibrary.agedAtMost(35);
		context.addParameterValue("effectiveDate", context.getParameterValue("endDate"));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);

		// #6 will be 5, #7 will be 35, #8 has no birthdate, #999 is voided
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6, 7), evaluated);
	}

	/**
	 * @see CommonCohortLibrary#agedAtLeast(int)
	 */
	@Test
	public void ageAtLeast_shouldReturnAllPatientsAgedAtLeast() throws Exception {
		CohortDefinition cd = commonCohortLibrary.agedAtLeast(35);
		context.addParameterValue("effectiveDate", context.getParameterValue("endDate"));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);

		// #7 will be 35, #8 has no birthdate, #999 is voided
		ReportingTestUtils.assertCohortEquals(Arrays.asList(2, 7), evaluated);
	}

	/**
	 * @see CommonCohortLibrary#agedAtLeast(int)
	 */
	@Test
	public void femalesAgedAtLeast18_shouldReturnAllPatientsAgedAtLeast() throws Exception {
		CohortDefinition cd = commonCohortLibrary.femalesAgedAtLeast18();
		context.addParameterValue("effectiveDate", context.getParameterValue("endDate"));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);

		// #7 will be 35, #8 has no birthdate, #999 is voided
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);
	}

	/**
	 * @see CommonCohortLibrary#hasEncounter(org.openmrs.EncounterType...)
	 */
	@Test
	public void hasEncounter() throws Exception {
		EncounterType registrationType = MetadataUtils.existing(EncounterType.class, CommonMetadata._EncounterType.REGISTRATION);
		EncounterType triageType = MetadataUtils.existing(EncounterType.class, CommonMetadata._EncounterType.TRIAGE);

		// Give #6 registration encounter on June 1st
		TestUtils.saveEncounter(Context.getPatientService().getPatient(6), registrationType, TestUtils.date(2012, 6, 1));

		// Give #7 registration encounter on July 1st
		TestUtils.saveEncounter(Context.getPatientService().getPatient(7), registrationType, TestUtils.date(2012, 7, 1));

		// Give #8 triage encounter on June 1st
		TestUtils.saveEncounter(Context.getPatientService().getPatient(8), triageType, TestUtils.date(2012, 6, 1));

		CohortDefinition cd = commonCohortLibrary.hasEncounter(registrationType);
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}

	/**
	 * @see CommonCohortLibrary#enrolled(org.openmrs.Program...)
	 */
	@Test
	public void enrolled() throws Exception {
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		// Enroll patient 2 on May 31st
		TestUtils.enrollInProgram(Context.getPatientService().getPatient(2), hivProgram, TestUtils.date(2012, 5, 31));

		// Enroll patient 6 on June 1st
		TestUtils.enrollInProgram(Context.getPatientService().getPatient(6), hivProgram, TestUtils.date(2012, 6, 1));

		// Enroll patient 7 on June 30th
		TestUtils.enrollInProgram(Context.getPatientService().getPatient(7), hivProgram, TestUtils.date(2012, 6, 30));

		// Enroll patient 8 on July 1st
		TestUtils.enrollInProgram(Context.getPatientService().getPatient(8), hivProgram, TestUtils.date(2012, 7, 1));

		// Check with startDate only
		CohortDefinition cd = commonCohortLibrary.enrolled(hivProgram);
		context.addParameterValue("enrolledOnOrAfter", TestUtils.date(2012, 6, 1));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6, 7, 8), evaluated);

		// Check with endDate only
		cd = commonCohortLibrary.enrolled(hivProgram);
		context.addParameterValue("enrolledOnOrBefore", TestUtils.date(2012, 6, 30));
		context.addParameterValue("enrolledOnOrAfter", null);
		evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(2, 6, 7), evaluated);

		// Check both
		cd = commonCohortLibrary.enrolled(hivProgram);
		context.addParameterValue("enrolledOnOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("enrolledOnOrBefore", TestUtils.date(2012, 6, 30));
		evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6, 7), evaluated);
	}

	/**
	 * @see CommonCohortLibrary#transferredIn()
	 */
	@Test
	public void transferredIn() throws Exception {
		Concept transferInDate = Dictionary.getConcept(Dictionary.TRANSFER_IN_DATE);

		// Transfer in #6 on June 1st (obs recorded on July 1st)
		TestUtils.saveObs(Context.getPatientService().getPatient(6), transferInDate, TestUtils.date(2012, 6, 1),  TestUtils.date(2012, 7, 1));

		// Transfer in #7 on July 1st (obs recorded on June 1st)
		TestUtils.saveObs(Context.getPatientService().getPatient(6), transferInDate, TestUtils.date(2012, 7, 1),  TestUtils.date(2012, 6, 1));

		CohortDefinition cd = commonCohortLibrary.transferredIn();
		context.addParameterValue("value2", TestUtils.date(2012, 6, 30));

		System.out.println(cd);

		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}

	/**
	 * @see CommonCohortLibrary#enrolledExcludingTransfers(org.openmrs.Program...)
	 */
	@Test
	public void enrolledExcludingTransfers() throws Exception {
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Concept transferInDate = Dictionary.getConcept(Dictionary.TRANSFER_IN_DATE);

		// Enroll #6 on June 1st
		TestUtils.enrollInProgram(TestUtils.getPatient(6), hivProgram, TestUtils.date(2012, 6, 1));

		// Enroll #7 on June 1st as a transfer in
		TestUtils.enrollInProgram(TestUtils.getPatient(7), hivProgram, TestUtils.date(2012, 6, 1));
		TestUtils.saveObs(TestUtils.getPatient(7), transferInDate, TestUtils.date(2012, 6, 1),  TestUtils.date(2012, 6, 1));

		// Enroll #8 on July 1st
		TestUtils.enrollInProgram(TestUtils.getPatient(8), hivProgram, TestUtils.date(2012, 7, 1));

		CohortDefinition cd = commonCohortLibrary.enrolledExcludingTransfers(hivProgram);
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}

	/**
	 * @see CommonCohortLibrary#medicationDispensed(org.openmrs.Concept...)
	 */
	@Test
	public void medicationDispensed() throws Exception {
		Concept medsOrdered = Dictionary.getConcept(Dictionary.MEDICATION_ORDERS);
		Concept dapsone = Dictionary.getConcept(Dictionary.DAPSONE);
		Concept ctx = Dictionary.getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM);

		// Give patient #6 dispensing record for CTX during reporting period
		TestUtils.saveObs(TestUtils.getPatient(6), medsOrdered, ctx, TestUtils.date(2012, 6, 15));

		// Give patient #7 dispensing record for Dapsone during reporting period
		TestUtils.saveObs(TestUtils.getPatient(7), medsOrdered, dapsone, TestUtils.date(2012, 6, 15));

		// Give patient #8 dispensing record for Dapsone after reporting period
		TestUtils.saveObs(TestUtils.getPatient(8), medsOrdered, dapsone, TestUtils.date(2012, 7, 1));

		CohortDefinition cd = commonCohortLibrary.medicationDispensed(dapsone);
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);
	}
}