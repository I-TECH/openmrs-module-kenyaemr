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

package org.openmrs.module.kenyaemr.reporting.library.shared.tb;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.test.ReportingTestUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
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
 * Tests for {@link org.openmrs.module.kenyaemr.reporting.library.shared.tb.TbCohortLibrary}
 */
public class TbCohortLibraryTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private TbMetadata tbMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private TbCohortLibrary tbCohortLibrary;

	private EvaluationContext context;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		tbMetadata.install();
		hivMetadata.install();

		Concept tbDiseaseStatus = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DISEASE_STATUS);
		Concept diseaseSuspected = Dictionary.getConcept(Dictionary.DISEASE_SUSPECTED);
		Concept notAssessed = Dictionary.getConcept(Dictionary.NOT_ASSESSED);

		// Screen patient #2 on May 31st
		TestUtils.saveObs(TestUtils.getPatient(2), tbDiseaseStatus, diseaseSuspected, TestUtils.date(2012, 5, 31));

		// Screen patient #6 on June 1st
		TestUtils.saveObs(TestUtils.getPatient(6), tbDiseaseStatus, diseaseSuspected, TestUtils.date(2012, 6, 1));

		// Screen patient #7 on June 30th
		TestUtils.saveObs(TestUtils.getPatient(7), tbDiseaseStatus, diseaseSuspected, TestUtils.date(2012, 6, 30));

		// Record not-screening patient #8 on June 30th
		TestUtils.saveObs(TestUtils.getPatient(7), tbDiseaseStatus, notAssessed, TestUtils.date(2012, 6, 30));

		// Record screening patient #8 on July 1st
		TestUtils.saveObs(TestUtils.getPatient(8), tbDiseaseStatus, diseaseSuspected, TestUtils.date(2012, 7, 1));

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

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.library.shared.tb.TbCohortLibrary#died()
	 */
	@Test
	public void died_shouldReturnPatientsWhoDiedBetweenDates() throws Exception {
		Concept tbTreatmentOutcome = Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME);
		Concept died = Dictionary.getConcept(Dictionary.DIED);

		// Exit patient #6 as died on May 15th (before reporting period)
		TestUtils.saveObs(TestUtils.getPatient(6), tbTreatmentOutcome, died, TestUtils.date(2012, 5, 15));

		// Exit patient #7 as died on June 15th
		TestUtils.saveObs(TestUtils.getPatient(7), tbTreatmentOutcome, died, TestUtils.date(2012, 6, 15));

		// Exit patient #8 as died on July 15th (after reporting period)
		TestUtils.saveObs(TestUtils.getPatient(8), tbTreatmentOutcome, died, TestUtils.date(2012, 7, 15));

		// Check with both start and end date
		CohortDefinition cd = tbCohortLibrary.died();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);
	}

	/**
	 * @see TbCohortLibrary#started12MonthsAgo()
	 */
	@Ignore
	@Test
	public void started12MonthsAgo_shouldReturnPatientsWhoStartedTreatment12MonthsAgo() throws Exception {
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);

		// Enroll patient #6 on May 15th 2011
		TestUtils.enrollInProgram(TestUtils.getPatient(6), tbProgram, TestUtils.date(2011, 5, 15));

		// Enroll patient #7 on June 15th 2011
		TestUtils.enrollInProgram(TestUtils.getPatient(7), tbProgram, TestUtils.date(2011, 6, 15));

		// Enroll patient #8 on July 15th 2011
		TestUtils.enrollInProgram(TestUtils.getPatient(8), tbProgram, TestUtils.date(2011, 7, 15));

		// Check with both start and end date
		CohortDefinition cd = tbCohortLibrary.started12MonthsAgo();
		context.addParameterValue("onDate", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.reporting.library.shared.tb.TbCohortLibrary#diedAndStarted12MonthsAgo()
	 */
	@Ignore
	@Test
	public void diedStarted12MonthsAgo_shouldReturnPatientsWhoDiedAndStartedTreatment12MonthsAgo() throws Exception {
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
		Concept tbTreatmentOutcome = Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME);
		Concept tbStartDate = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DRUG_TREATMENT_START_DATE);
		Concept died = Dictionary.getConcept(Dictionary.DIED);

		// Enroll patient #2 on May 15th 2011
		TestUtils.enrollInProgram(TestUtils.getPatient(2), tbProgram, TestUtils.date(2011, 5, 15));

		// Enroll patients #6 and #7 on June 15th 2011
		TestUtils.enrollInProgram(TestUtils.getPatient(6), tbProgram, TestUtils.date(2011, 6, 15));
		TestUtils.enrollInProgram(TestUtils.getPatient(7), tbProgram, TestUtils.date(2011, 6, 15));

		// Exit patient #7 as died on June 15th 2012
		TestUtils.saveObs(TestUtils.getPatient(7), tbStartDate, TestUtils.date(2011, 6, 10), TestUtils.date(2011, 6, 20));
		TestUtils.saveObs(TestUtils.getPatient(7), tbTreatmentOutcome, died, TestUtils.date(2012, 6, 15));

		// Exit patient #8 as died on June 15th 2012
		TestUtils.saveObs(TestUtils.getPatient(8), tbTreatmentOutcome, died, TestUtils.date(2012, 6, 15));

		// Check with both start and end date
		CohortDefinition cd = tbCohortLibrary.diedAndStarted12MonthsAgo();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);
	}

	/**
	 * @see TbCohortLibrary#completedTreatment()
	 */
	@Test
	public void completedTreatment_shouldReturnPatientsWhoCompletedTreatmentBetweenDates() throws Exception {

		Concept tbTreatmentOutcome = Dictionary.getConcept(Dictionary.TUBERCULOSIS_TREATMENT_OUTCOME);
		Concept completed = Dictionary.getConcept(Dictionary.TREATMENT_COMPLETE);

		// Exit patient #6 as died on May 15th (before reporting period)
		TestUtils.saveObs(TestUtils.getPatient(6), tbTreatmentOutcome, completed, TestUtils.date(2012, 5, 15));

		// Exit patient #7 as died on June 15th
		TestUtils.saveObs(TestUtils.getPatient(7), tbTreatmentOutcome, completed, TestUtils.date(2012, 6, 15));

		// Exit patient #8 as died on July 15th (after reporting period)
		TestUtils.saveObs(TestUtils.getPatient(8), tbTreatmentOutcome, completed, TestUtils.date(2012, 7, 15));

		// Check with both start and end date
		CohortDefinition cd = tbCohortLibrary.completedTreatment();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);
	}


	/**
	 * @see TbCohortLibrary#inTbAndHivProgramsAndOnCtxProphylaxis()
	 */
	@Test
	public void inTbAndHivProgramsAndOnCtxProphylaxis_shouldReturnPatientsInTbAndHivProgramsAndOnCtx() throws Exception {

		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);

		//Enroll patient #2 into Tb program given they are already in hiv program in std dataset

		TestUtils.enrollInProgram(TestUtils.getPatient(2), tbProgram, TestUtils.date(2012, 6, 10));

		// Enroll patient #6 on May 15th 2011 in tb program
		TestUtils.enrollInProgram(TestUtils.getPatient(6),  hivProgram, TestUtils.date(2011, 5, 10));

		// Enroll patient #7 in both programs
		TestUtils.enrollInProgram(TestUtils.getPatient(7), tbProgram, TestUtils.date(2012, 6, 10));
		TestUtils.enrollInProgram(TestUtils.getPatient(7), hivProgram, TestUtils.date(2012, 6, 10));

		// Put patient #7 on ctx
		VisitType outpatientType = MetadataUtils.existing(VisitType.class, CommonMetadata._VisitType.OUTPATIENT);
		EncounterType consultationType = MetadataUtils.existing(EncounterType.class, CommonMetadata._EncounterType.CONSULTATION);
		Concept medOrders = Dictionary.getConcept(Dictionary.MEDICATION_ORDERS);
		Concept ctx = Dictionary.getConcept(Dictionary.SULFAMETHOXAZOLE_TRIMETHOPRIM);

		TestUtils.saveVisit(TestUtils.getPatient(7), outpatientType, TestUtils.date(2012, 6, 10), TestUtils.date(2012, 6, 10),
		TestUtils.saveEncounter(TestUtils.getPatient(7), consultationType, TestUtils.date(2012, 6, 10),
		TestUtils.saveObs(TestUtils.getPatient(7), medOrders, ctx, TestUtils.date(2012, 6, 10))
			)
		);

		CohortDefinition cd = tbCohortLibrary.inTbAndHivProgramsAndOnCtxProphylaxis();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);
	}

	/**
	 * @see TbCohortLibrary#testedForHivAndInTbProgram()
	 */
	@Test
	public void testedForHivAndInTbProgram_shouldReturnPatientsWhoTestedForHivAndInTbProgram() throws Exception {
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Concept hivStatus = Dictionary.getConcept(Dictionary.HIV_STATUS);
		Concept hivInfected = Dictionary.getConcept(Dictionary.HIV_INFECTED);
		Concept positive = Dictionary.getConcept(Dictionary.POSITIVE);
		Concept negative = Dictionary.getConcept(Dictionary.NEGATIVE);

		//Enroll patient #2 into Tb program given they are already in hiv program in std dataset
		TestUtils.enrollInProgram(TestUtils.getPatient(2), tbProgram, TestUtils.date(2012, 6, 10));

		// Enroll patient #6 on May 15th 2011 in tb program should NOT pass the test
		TestUtils.enrollInProgram(TestUtils.getPatient(6),  hivProgram, TestUtils.date(2011, 5, 10));

		// Enroll patient #7 in tb programs
		TestUtils.enrollInProgram(TestUtils.getPatient(7), tbProgram, TestUtils.date(2012, 6, 8));

		// Enroll patient #8 in tb programs
		TestUtils.enrollInProgram(TestUtils.getPatient(8), tbProgram, TestUtils.date(2012, 6, 10));

		//#6 to have obs hivStatus negative
		TestUtils.saveObs(TestUtils.getPatient(6), hivStatus, negative, TestUtils.date(2012, 6, 11));
		//#7 to have obs hivInfected positive
		TestUtils.saveObs(TestUtils.getPatient(7), hivInfected, positive, TestUtils.date(2012, 6, 14));
		//#2 to have obs hivInfected positive
		TestUtils.saveObs(TestUtils.getPatient(2), hivInfected, positive, TestUtils.date(2012, 6, 19));
		//#8 to have obs hivInfected negative
		TestUtils.saveObs(TestUtils.getPatient(8), hivInfected, negative, TestUtils.date(2012, 6, 17));

		CohortDefinition cd = tbCohortLibrary.testedForHivAndInTbProgram();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(2,7,8), evaluated);
	}

	/**
	 * @see TbCohortLibrary#testedHivPositiveAndInTbProgram()
	 */
	@Test
	public void testedHivPositiveAndInTbProgram_shouldReturnPatientsWhoTestedHivPositiveAndInTbProgram() throws Exception {
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Concept hivStatus = Dictionary.getConcept(Dictionary.HIV_STATUS);
		Concept hivInfected = Dictionary.getConcept(Dictionary.HIV_INFECTED);
		Concept positive = Dictionary.getConcept(Dictionary.POSITIVE);
		Concept negative = Dictionary.getConcept(Dictionary.NEGATIVE);

		//Enroll patient #2 into Tb program given they are already in hiv program in std dataset
		TestUtils.enrollInProgram(TestUtils.getPatient(2), tbProgram, TestUtils.date(2012, 6, 10));

		// Enroll patient #6 on May 15th 2011 in tb program should NOT pass the test
		TestUtils.enrollInProgram(TestUtils.getPatient(6),  hivProgram, TestUtils.date(2011, 5, 10));

		// Enroll patient #7 in both programs
		TestUtils.enrollInProgram(TestUtils.getPatient(7), tbProgram, TestUtils.date(2012, 6, 10));

		//#6 to have obs hivStatus negative
		TestUtils.saveObs(TestUtils.getPatient(6), hivStatus, negative, TestUtils.date(2012, 6, 10));
		//#7 to have obs hivInfected positive
		TestUtils.saveObs(TestUtils.getPatient(7), hivInfected, positive, TestUtils.date(2012, 6, 10));
		//#2 to have obs hivInfected positive
		TestUtils.saveObs(TestUtils.getPatient(2), hivInfected, positive, TestUtils.date(2012, 6, 10));
		//#8 to have obs hivInfected negative
		TestUtils.saveObs(TestUtils.getPatient(8), hivInfected, negative, TestUtils.date(2012, 6, 17));

		CohortDefinition cd = tbCohortLibrary.testedHivPositiveAndInTbProgram();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(2,7), evaluated);
	}

	/**
	 * @see TbCohortLibrary#tbRetreatments()
	 */
	@Test
	public void tbRetreatments_shouldReturnPatientsWhoTbRetreatments() throws Exception {
		Concept patientClassification = Dictionary.getConcept(Dictionary.TYPE_OF_TB_PATIENT);
		Concept retreatment = Dictionary.getConcept(Dictionary.RETREATMENT_AFTER_DEFAULT_TUBERCULOSIS);
		Concept transferIn = Dictionary.getConcept(Dictionary.TRANSFER_IN_TUBERCULOSIS_PATIENT);
		//#7 to have obs patientClassification retreatment
		TestUtils.saveObs(TestUtils.getPatient(7), patientClassification, retreatment, TestUtils.date(2012, 6, 10));
		//#6 to have obs patientClassification transfer in
		TestUtils.saveObs(TestUtils.getPatient(6), patientClassification, transferIn, TestUtils.date(2012, 6, 10));
		// Check with both start and end date
		CohortDefinition cd = tbCohortLibrary.tbRetreatments();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);
	}

	/**
	 * @see TbCohortLibrary#extraPulmonaryTbPatients()
	 */
	@Test
	public void extraPulmonaryTbPatients_shouldReturnPatientsWhoHaveExtraPulmonaryTbPatients() throws Exception {
		Concept diseaseClassification = Dictionary.getConcept(Dictionary.SITE_OF_TUBERCULOSIS_DISEASE);
		Concept extraPulmonary = Dictionary.getConcept(Dictionary.MYCROBACTERIUM_TUBERCULOSIS_EXTRAPULMONARY);
		Concept pulmonary = Dictionary.getConcept(Dictionary.PULMONARY_TB);
		//#7 to have obs diseaseClassification extraPulmonary
		TestUtils.saveObs(TestUtils.getPatient(7), diseaseClassification, extraPulmonary, TestUtils.date(2012, 6, 10));
		//#6 to have obs patientClassification transfer in
		TestUtils.saveObs(TestUtils.getPatient(6), diseaseClassification, pulmonary, TestUtils.date(2012, 6, 10));
		// Check with both start and end date
		CohortDefinition cd = tbCohortLibrary.extraPulmonaryTbPatients();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);
	}

	/**
	 * @see TbCohortLibrary#pulmonaryTbPatients()
	 */
	@Test
	public void pulmonaryTbPatients_shouldReturnPatientsWhoHavePulmonaryTbPatients() throws Exception {
		Concept diseaseClassification = Dictionary.getConcept(Dictionary.SITE_OF_TUBERCULOSIS_DISEASE);
		Concept extraPulmonary = Dictionary.getConcept(Dictionary.MYCROBACTERIUM_TUBERCULOSIS_EXTRAPULMONARY);
		Concept pulmonary = Dictionary.getConcept(Dictionary.PULMONARY_TB);
		//#7 to have obs diseaseClassification extraPulmonary
		TestUtils.saveObs(TestUtils.getPatient(7), diseaseClassification, extraPulmonary, TestUtils.date(2012, 6, 10));
		//#6 to have obs patientClassification  pulmonary
		TestUtils.saveObs(TestUtils.getPatient(6), diseaseClassification, pulmonary, TestUtils.date(2012, 6, 10));
		// Check with both start and end date
		CohortDefinition cd = tbCohortLibrary.pulmonaryTbPatients();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}

	/**
	 * @see TbCohortLibrary#smearNegativePatients()
	 */
	@Test
	public void smearNegativePatients_shouldReturnPatientsWithSmearNegativePatients() throws Exception {
		Concept cultureResults = Dictionary.getConcept(Dictionary.RESULTS_TUBERCULOSIS_CULTURE);
		Concept smearNegative = Dictionary.getConcept(Dictionary.NEGATIVE);
		Concept smearPositive = Dictionary.getConcept(Dictionary.POSITIVE);

		//#7 to have obs cultureResults smearNegative
		TestUtils.saveObs(TestUtils.getPatient(7), cultureResults, smearNegative, TestUtils.date(2012, 6, 10));
		//#6 to have obs cultureResults smearPositive
		TestUtils.saveObs(TestUtils.getPatient(6), cultureResults, smearPositive, TestUtils.date(2012, 6, 10));

		// Check with both start and end date
		CohortDefinition cd = tbCohortLibrary.smearNegativePatients();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);

	}

	/**
	 * @see TbCohortLibrary#smearPositivePatients()
	 */
	@Test
	public void smearPositivePatients_shouldReturnPatientsWithSmearPositive() throws Exception {
		Concept cultureResults = Dictionary.getConcept(Dictionary.RESULTS_TUBERCULOSIS_CULTURE);
		Concept smearNegative = Dictionary.getConcept(Dictionary.NEGATIVE);
		Concept smearPositive = Dictionary.getConcept(Dictionary.POSITIVE);

		//#7 to have obs cultureResults smearNegative
		TestUtils.saveObs(TestUtils.getPatient(7), cultureResults, smearNegative, TestUtils.date(2012, 6, 10));
		//#6 to have obs cultureResults smearPositive
		TestUtils.saveObs(TestUtils.getPatient(6), cultureResults, smearPositive, TestUtils.date(2012, 6, 10));

		// Check with both start and end date
		CohortDefinition cd = tbCohortLibrary.smearPositivePatients();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);

	}

	/**
	 * @see TbCohortLibrary#pulmonaryTbSmearNegative
	 */
	@Test
	public void pulmonaryTbSmearNegative_shouldReturnPatientsWithPulmonaryTbSmearNegative() throws Exception{
		Concept diseaseClassification = Dictionary.getConcept(Dictionary.SITE_OF_TUBERCULOSIS_DISEASE);
		Concept cultureResults = Dictionary.getConcept(Dictionary.RESULTS_TUBERCULOSIS_CULTURE);
		Concept extraPulmonary = Dictionary.getConcept(Dictionary.MYCROBACTERIUM_TUBERCULOSIS_EXTRAPULMONARY);
		Concept pulmonary = Dictionary.getConcept(Dictionary.PULMONARY_TB);
		Concept smearNegative = Dictionary.getConcept(Dictionary.NEGATIVE);
		Concept smearPositive = Dictionary.getConcept(Dictionary.POSITIVE);

		//#6 to have obs diseaseClassification  pulmonary
		TestUtils.saveObs(TestUtils.getPatient(6), diseaseClassification, pulmonary, TestUtils.date(2012, 6, 10));
		//#6 to have obs cultureResults smearNegative
		TestUtils.saveObs(TestUtils.getPatient(6), cultureResults, smearNegative, TestUtils.date(2012, 6, 11));
		//#7 to have obs patientClassification  pulmonary
		TestUtils.saveObs(TestUtils.getPatient(7), diseaseClassification, pulmonary, TestUtils.date(2012, 6, 10));
		//#7 to have obs cultureResults smearpositive
		TestUtils.saveObs(TestUtils.getPatient(7), cultureResults, smearPositive, TestUtils.date(2012, 6, 11));
		//#2 to have obs  diseaseClassification extra pulmonary
		TestUtils.saveObs(TestUtils.getPatient(2), diseaseClassification, extraPulmonary, TestUtils.date(2012, 6, 10));

		// Check with both start and end date
		CohortDefinition cd = tbCohortLibrary.pulmonaryTbSmearNegative();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}

	/**
	 * @see TbCohortLibrary#pulmonaryTbSmearPositive()
	 */
	@Test
	public void pulmonaryTbSmearPositive_shouldReturnPatientsWithPulmonaryTbSmearPositive() throws Exception{
		Concept diseaseClassification = Dictionary.getConcept(Dictionary.SITE_OF_TUBERCULOSIS_DISEASE);
		Concept cultureResults = Dictionary.getConcept(Dictionary.RESULTS_TUBERCULOSIS_CULTURE);
		Concept extraPulmonary = Dictionary.getConcept(Dictionary.MYCROBACTERIUM_TUBERCULOSIS_EXTRAPULMONARY);
		Concept pulmonary = Dictionary.getConcept(Dictionary.PULMONARY_TB);
		Concept smearNegative = Dictionary.getConcept(Dictionary.NEGATIVE);
		Concept smearPositive = Dictionary.getConcept(Dictionary.POSITIVE);

		//#6 to have obs diseaseClassification  pulmonary
		TestUtils.saveObs(TestUtils.getPatient(6), diseaseClassification, pulmonary, TestUtils.date(2012, 6, 10));
		//#6 to have obs cultureResults smearNegative
		TestUtils.saveObs(TestUtils.getPatient(6), cultureResults, smearNegative, TestUtils.date(2012, 6, 11));
		//#7 to have obs patientClassification  pulmonary
		TestUtils.saveObs(TestUtils.getPatient(7), diseaseClassification, pulmonary, TestUtils.date(2012, 6, 10));
		//#7 to have obs cultureResults smearpositive
		TestUtils.saveObs(TestUtils.getPatient(7), cultureResults, smearPositive, TestUtils.date(2012, 6, 11));
		//#2 to have obs  diseaseClassification extra pulmonary
		TestUtils.saveObs(TestUtils.getPatient(2), diseaseClassification, extraPulmonary, TestUtils.date(2012, 6, 10));

		// Check with both start and end date
		CohortDefinition cd = tbCohortLibrary.pulmonaryTbSmearPositive();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);
	}

	/**
	 * @see TbCohortLibrary#tbNewDetectedCases()
	 */
	@Test
	public void tbNewDetectedCases_shouldReturnPatientsWhoHaveTbNewDetectedCases() throws Exception {
		Concept patientClassification = Dictionary.getConcept(Dictionary.TYPE_OF_TB_PATIENT);
		Concept newdetects = Dictionary.getConcept(Dictionary.SMEAR_POSITIVE_NEW_TUBERCULOSIS_PATIENT);
		Concept transferIn = Dictionary.getConcept(Dictionary.TRANSFER_IN_TUBERCULOSIS_PATIENT);
		//#7 to have obs patientClassification newdetects
		TestUtils.saveObs(TestUtils.getPatient(7), patientClassification, newdetects, TestUtils.date(2012, 6, 10));
		//#6 to have obs patientClassification transfer in
		TestUtils.saveObs(TestUtils.getPatient(6), patientClassification, transferIn, TestUtils.date(2012, 6, 10));
		// Check with both start and end date
		CohortDefinition cd = tbCohortLibrary.tbNewDetectedCases();
		context.addParameterValue("onOrAfter", TestUtils.date(2012, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2012, 6, 30));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);
	}

	/**
	 * @see TbCohortLibrary#totalEnrolledPtbSmearNotDoneResultsAtMonths(int, int)
	 */
	@Ignore
	@Test
	public  void ptbSmearNotDoneResultsAtMonths_totalEnrolledPtbSmearNotDoneResultsAtMonths() throws Exception {
		Concept diseaseClassification = Dictionary.getConcept(Dictionary.SITE_OF_TUBERCULOSIS_DISEASE);
		Concept extraPulmonary = Dictionary.getConcept(Dictionary.MYCROBACTERIUM_TUBERCULOSIS_EXTRAPULMONARY);
		Concept pulmonary = Dictionary.getConcept(Dictionary.PULMONARY_TB);
		Concept smearNotDone = Dictionary.getConcept(Dictionary.NOT_DONE);
		Concept cultureResults = Dictionary.getConcept(Dictionary.RESULTS_TUBERCULOSIS_CULTURE);
		//get the tb start treatment date as a concept
		Concept tbStartDate = Dictionary.getConcept(Dictionary.TUBERCULOSIS_DRUG_TREATMENT_START_DATE);
		//enroll patient 6 into tb program 12 months ago from 31/07/2014
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);
		TestUtils.enrollInProgram(TestUtils.getPatient(6), tbProgram, TestUtils.date(2013, 7, 31));

		//#7 to have obs diseaseClassification extraPulmonary
		TestUtils.saveObs(TestUtils.getPatient(7), diseaseClassification, extraPulmonary, TestUtils.date(2014, 7, 10));
		//#6 to have obs patientClassification  pulmonary and give him results smear Not done
		TestUtils.saveObs(TestUtils.getPatient(6), diseaseClassification, pulmonary, TestUtils.date(2014, 7, 10));
		TestUtils.saveObs(TestUtils.getPatient(6), cultureResults, smearNotDone, TestUtils.date(2014, 7, 28));
		TestUtils.saveObs(TestUtils.getPatient(6), tbStartDate, TestUtils.date(2014, 6, 1), TestUtils.date(2014, 7, 28));

		CohortDefinition cd = tbCohortLibrary.totalEnrolledPtbSmearNotDoneResultsAtMonths(12, 8);
		context.addParameterValue("onOrAfter", TestUtils.date(2014, 7, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2014, 7, 31));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}
}