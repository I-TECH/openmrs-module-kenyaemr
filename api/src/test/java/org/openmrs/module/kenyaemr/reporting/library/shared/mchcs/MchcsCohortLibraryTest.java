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
import org.openmrs.module.kenyacore.test.ReportingTestUtils;
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
		context = ReportingTestUtils.reportingContext(Arrays.asList(2, 6, 7, 8, 999), TestUtils.date(2012, 6, 1), TestUtils.date(2012, 6, 30));
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

	/**
	 * @see MchcsCohortLibrary#age2Months()
	 */
	@Test
	public void  age2Months_shouldReturnInfantsAged2Months() throws Exception {
		CohortDefinition cd = mchcsCohortLibrary.age2Months();
		context.addParameterValue("effectiveDate", TestUtils.date(2007, 7, 1));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);

	}

	/**
	 * @see MchcsCohortLibrary#pcrInitialWithin2Months()
	 */
	@Test
	public void pcrInitialWithin2Months_shouldReturnPatientsWithPcrInitialWithin2Months() throws Exception {
		Concept pcrTest = Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION);
		Concept detected = Dictionary.getConcept(Dictionary.DETECTED);
		Concept hivTest = Dictionary.getConcept(Dictionary.HIV_STATUS);
		Concept positive = Dictionary.getConcept(Dictionary.POSITIVE);
		Concept poorSampleQuality = Dictionary.getConcept(Dictionary.POOR_SAMPLE_QUALITY);
		Concept contexualStatus = Dictionary.getConcept(Dictionary.TEXT_CONTEXT_STATUS);
		Concept initial = Dictionary.getConcept(Dictionary.TEST_STATUS_INITIAL);
		Concept complete = Dictionary.getConcept(Dictionary.CONFIRMATION_STATUS);

		//make patient #6 to have a pcr test with the results as detected
		TestUtils.saveObs(TestUtils.getPatient(6), pcrTest, detected, TestUtils.date(2007, 7, 1));
		//make patient #7 to have a pcr test with result poor sample
		TestUtils.saveObs(TestUtils.getPatient(7), pcrTest, poorSampleQuality, TestUtils.date(2013, 6, 15));
		//get the hiv status of #8 and positive
		TestUtils.saveObs(TestUtils.getPatient(8), hivTest, positive, TestUtils.date(2013, 6, 20));
		//only #6 and #7 should pass

		//make patient #6 to have a pcr test with the results as detected
		TestUtils.saveObs(TestUtils.getPatient(6), contexualStatus, initial, TestUtils.date(2007, 7, 1));
		//make patient #7 to have a pcr test with result poor sample
		TestUtils.saveObs(TestUtils.getPatient(7), contexualStatus, complete, TestUtils.date(2013, 6, 15));
		//only #6 to qualify

		CohortDefinition cd = mchcsCohortLibrary.pcrInitialWithin2Months();
		context.addParameterValue("onOrAfter", TestUtils.date(2007, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2007, 7, 1));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}

	/**
	 * @see MchcsCohortLibrary#ageBetween3And8Months()
	 */
	@Test
	public void ageBetween3And8Months_shouldReturnInfantsAgeBetween3And8Months() throws Exception {

		CohortDefinition cd = mchcsCohortLibrary.ageBetween3And8Months();
		context.addParameterValue("effectiveDate", TestUtils.date(2007, 10, 1));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}

	/**
	 * @see MchcsCohortLibrary#pcrInitialBetween3To8Months()
	 */
	@Test
	public void pcrInitialBetween3To8Months_shouldReturnPatientsWithPcrInitialBetween3To8Months() throws Exception{
		Concept pcrTest = Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION);
		Concept detected = Dictionary.getConcept(Dictionary.DETECTED);
		Concept hivTest = Dictionary.getConcept(Dictionary.HIV_STATUS);
		Concept positive = Dictionary.getConcept(Dictionary.POSITIVE);
		Concept poorSampleQuality = Dictionary.getConcept(Dictionary.POOR_SAMPLE_QUALITY);
		Concept contexualStatus = Dictionary.getConcept(Dictionary.TEXT_CONTEXT_STATUS);
		Concept initial = Dictionary.getConcept(Dictionary.TEST_STATUS_INITIAL);
		Concept complete = Dictionary.getConcept(Dictionary.CONFIRMATION_STATUS);

		//make patient #6 to have a pcr test with the results as detected
		TestUtils.saveObs(TestUtils.getPatient(6), pcrTest, detected, TestUtils.date(2007, 10, 1));
		//make patient #7 to have a pcr test with result poor sample
		TestUtils.saveObs(TestUtils.getPatient(7), pcrTest, poorSampleQuality, TestUtils.date(2013, 6, 15));
		//get the hiv status of #8 and positive
		TestUtils.saveObs(TestUtils.getPatient(8), hivTest, positive, TestUtils.date(2013, 6, 20));
		//only #6 and #7 should pass

		//make patient #6 to have a pcr test with the results as detected
		TestUtils.saveObs(TestUtils.getPatient(6), contexualStatus, initial, TestUtils.date(2007, 10, 1));
		//make patient #7 to have a pcr test with result poor sample
		TestUtils.saveObs(TestUtils.getPatient(7), contexualStatus, complete, TestUtils.date(2013, 6, 15));
		//only #6 to qualify

		CohortDefinition cd = mchcsCohortLibrary.pcrInitialBetween3To8Months();
		context.addParameterValue("onOrAfter", TestUtils.date(2007, 9, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2007, 10, 1));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}

	/**
	 * @see MchcsCohortLibrary#serologyAntBodyTest()
	 */
	 @Test
	public void serologyAntBodyTest_shouldReturnInfantsWithSerologyAntBodyTest() throws Exception {
		 Concept hivRapidTest = Dictionary.getConcept(Dictionary.HIV_RAPID_TEST_1_QUALITATIVE);
		 Concept negative = Dictionary.getConcept(Dictionary.NEGATIVE);
		 Concept poorSampleQuality = Dictionary.getConcept(Dictionary.POOR_SAMPLE_QUALITY);
		 Concept unknown = Dictionary.getConcept(Dictionary.UNKNOWN);

		 TestUtils.saveObs(TestUtils.getPatient(6), hivRapidTest, negative, TestUtils.date(2012, 6, 10));
		 TestUtils.saveObs(TestUtils.getPatient(7), hivRapidTest, unknown, TestUtils.date(2012, 6, 15));
		 TestUtils.saveObs(TestUtils.getPatient(8), hivRapidTest, poorSampleQuality, TestUtils.date(2012, 6, 20));

		 CohortDefinition cd = mchcsCohortLibrary.serologyAntBodyTest();
		 EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		 ReportingTestUtils.assertCohortEquals(Arrays.asList(6,8), evaluated);
	 }

	/**
	 * @see MchcsCohortLibrary#ageBetween9And12Months()
	 */
	@Test
	public void ageBetween9And12Months_shouldReturnInfantsWithAgeBetween9And12Months() throws Exception {
		CohortDefinition cd = mchcsCohortLibrary.ageBetween9And12Months();
		context.addParameterValue("effectiveDate", TestUtils.date(2008, 5, 19));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}

	/**
	 * @see MchcsCohortLibrary#serologyAntBodyTestBetween9And12Months()
	 */
	 @Test
	 public void serologyAntBodyTestBetween9And12Months_shouldReturnPatientsWithSerologyAntBodyTestBetween9And12Months() throws Exception{

		 Concept hivRapidTest = Dictionary.getConcept(Dictionary.HIV_RAPID_TEST_1_QUALITATIVE);
		 Concept negative = Dictionary.getConcept(Dictionary.NEGATIVE);
		 Concept poorSampleQuality = Dictionary.getConcept(Dictionary.POOR_SAMPLE_QUALITY);
		 Concept unknown = Dictionary.getConcept(Dictionary.UNKNOWN);

		 TestUtils.saveObs(TestUtils.getPatient(6), hivRapidTest, negative, TestUtils.date(2008, 5, 19));
		 TestUtils.saveObs(TestUtils.getPatient(7), hivRapidTest, unknown, TestUtils.date(2012, 6, 15));
		 TestUtils.saveObs(TestUtils.getPatient(8), hivRapidTest, poorSampleQuality, TestUtils.date(2008, 5, 19));

		 CohortDefinition cd = mchcsCohortLibrary.serologyAntBodyTestBetween9And12Months();
		 context.addParameterValue("onOrAfter", TestUtils.date(2008, 4, 19));
		 context.addParameterValue("onOrBefore", TestUtils.date(2008, 5, 19));
		 EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		 ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	 }

	/**
	 * @see MchcsCohortLibrary#pcrBetween9And12MonthsAge()
	 */
	@Test
	public void pcrBetween9And12MonthsAge_shouldReturnPatientsWithPcrBetween9And12MonthsAge() throws Exception {
		Concept pcrTest = Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION);
		Concept detected = Dictionary.getConcept(Dictionary.DETECTED);
		Concept hivTest = Dictionary.getConcept(Dictionary.HIV_STATUS);
		Concept positive = Dictionary.getConcept(Dictionary.POSITIVE);
		Concept poorSampleQuality = Dictionary.getConcept(Dictionary.POOR_SAMPLE_QUALITY);
		Concept contexualStatus = Dictionary.getConcept(Dictionary.TEXT_CONTEXT_STATUS);
		Concept initial = Dictionary.getConcept(Dictionary.TEST_STATUS_INITIAL);
		Concept complete = Dictionary.getConcept(Dictionary.CONFIRMATION_STATUS);

		//make patient #6 to have a pcr test with the results as detected
		TestUtils.saveObs(TestUtils.getPatient(6), pcrTest, detected, TestUtils.date(2008, 5, 19));
		//make patient #7 to have a pcr test with result poor sample
		TestUtils.saveObs(TestUtils.getPatient(7), pcrTest, poorSampleQuality, TestUtils.date(2013, 6, 15));
		//get the hiv status of #8 and positive
		TestUtils.saveObs(TestUtils.getPatient(8), hivTest, positive, TestUtils.date(2013, 6, 20));
		//only #6 and #7 should pass

		//make patient #6 to have a pcr test with the results as detected
		TestUtils.saveObs(TestUtils.getPatient(6), contexualStatus, initial, TestUtils.date(2008, 5, 19));
		//make patient #7 to have a pcr test with result poor sample
		TestUtils.saveObs(TestUtils.getPatient(7), contexualStatus, complete, TestUtils.date(2013, 6, 15));
		//only #6 to qualify

		CohortDefinition cd = mchcsCohortLibrary.pcrBetween9And12MonthsAge();
		context.addParameterValue("onOrAfter", TestUtils.date(2008, 4, 19));
		context.addParameterValue("onOrBefore", TestUtils.date(2008, 5, 19));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}


	/**
	 * @see MchcsCohortLibrary#detectedConfirmedStatus()
	 */
	@Test
	public void detectedConfirmedStatus_shouldReturnInfantsWithDetectedConfirmedStatus() throws Exception {
		Concept testContextStatus = Dictionary.getConcept(Dictionary.TEXT_CONTEXT_STATUS) ;
		Concept detectedConfirmedStatus = Dictionary.getConcept(Dictionary.CONFIRMATION_STATUS);
		Concept poorSample = Dictionary.getConcept(Dictionary.POOR_SAMPLE_QUALITY);

		// give patient #7 a confirmed status
		TestUtils.saveObs(TestUtils.getPatient(7), testContextStatus, detectedConfirmedStatus,TestUtils.date(2012,6,10));
		// give patient #6 a poor sample status
		TestUtils.saveObs(TestUtils.getPatient(6), testContextStatus, poorSample,TestUtils.date(2012,6,15));
		CohortDefinition cd = mchcsCohortLibrary.detectedConfirmedStatus();
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);

	}

	/**
	 * @see MchcsCohortLibrary#ageAt6Months()
	 */
	@Test
	public void ageAt6Months_shouldReturnInfantsWithAgeAt6Months() throws Exception {
		CohortDefinition cd = mchcsCohortLibrary.ageAt6Months();
		context.addParameterValue("effectiveDate", TestUtils.date(1977, 3, 10));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(2,7), evaluated);
	}

	/**
	 * @see MchcsCohortLibrary#exclusiveBreastFeeding()
	 */
	@Test
	public void exclusiveBreastFeeding_shouldReturnInfantsWithExclusiveBreastFeeding() throws Exception {
		Concept infantFeedingMethod = Dictionary.getConcept(Dictionary.INFANT_FEEDING_METHOD);
		Concept exclusiveBreastFeeding = Dictionary.getConcept(Dictionary.BREASTFED_EXCLUSIVELY);
		Concept replacementFeeding = Dictionary.getConcept(Dictionary.REPLACEMENT_FEEDING);

		// give patient #7 an exclusive feeding method
		TestUtils.saveObs(TestUtils.getPatient(7), infantFeedingMethod, exclusiveBreastFeeding,TestUtils.date(2012,6,10));
		// give patient #6 a replacement feeding method
		TestUtils.saveObs(TestUtils.getPatient(6), infantFeedingMethod, replacementFeeding,TestUtils.date(2012,6,15));
		CohortDefinition cd = mchcsCohortLibrary.exclusiveBreastFeeding();
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);
	}

	/**
	 * @see MchcsCohortLibrary#exclusiveReplacementFeeding()
	 */
	@Test
	public void exclusiveReplacementFeeding_shouldReturnInfantWithExclusiveReplacementFeeding() throws Exception {
		Concept infantFeedingMethod = Dictionary.getConcept(Dictionary.INFANT_FEEDING_METHOD);
		Concept exclusiveBreastFeeding = Dictionary.getConcept(Dictionary.BREASTFED_EXCLUSIVELY);
		Concept replacementFeeding = Dictionary.getConcept(Dictionary.REPLACEMENT_FEEDING);

		// give patient #7 an exclusive feeding method
		TestUtils.saveObs(TestUtils.getPatient(7), infantFeedingMethod, exclusiveBreastFeeding,TestUtils.date(2012,6,10));
		// give patient #6 a replacement feeding method
		TestUtils.saveObs(TestUtils.getPatient(6), infantFeedingMethod, replacementFeeding,TestUtils.date(2012,6,15));
		CohortDefinition cd = mchcsCohortLibrary.exclusiveReplacementFeeding();
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}

	/**
	 * @see MchcsCohortLibrary#mixedFeeding()
	 */
	@Test
	public void mixedFeeding_shouldReturnInfantsWithMixedFeeding() throws Exception {
		Concept infantFeedingMethod = Dictionary.getConcept(Dictionary.INFANT_FEEDING_METHOD);
		Concept exclusiveBreastFeeding = Dictionary.getConcept(Dictionary.BREASTFED_EXCLUSIVELY);
		Concept replacementFeeding = Dictionary.getConcept(Dictionary.REPLACEMENT_FEEDING);
		Concept mixedFeeding = Dictionary.getConcept(Dictionary.MIXED_FEEDING);

		// give patient #7 an exclusive feeding method
		TestUtils.saveObs(TestUtils.getPatient(7), infantFeedingMethod, exclusiveBreastFeeding,TestUtils.date(2012,6,10));
		// give patient #6 a replacement feeding method
		TestUtils.saveObs(TestUtils.getPatient(6), infantFeedingMethod, replacementFeeding,TestUtils.date(2012,6,15));
		// give patient #8 a mixed feeding option
		TestUtils.saveObs(TestUtils.getPatient(8), infantFeedingMethod, mixedFeeding,TestUtils.date(2012,6,20));
		CohortDefinition cd = mchcsCohortLibrary.mixedFeeding();
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(8), evaluated);
	}

	/**
	 * @see MchcsCohortLibrary#motherOnTreatmentAndBreastFeeding()
	 */
	@Test
	public void motherOnTreatmentAndBreastFeeding_shouldReturnMotherOnTreatmentAndBreastFeeding() throws Exception {
		Concept motherOnTreatmentAndBreatFeeding = Dictionary.getConcept(Dictionary.MOTHER_ON_ANTIRETROVIRAL_DRUGS_AND_BREASTFEEDING);
		Concept breastfeeding = Dictionary.getConcept(Dictionary.YES);
		Concept notBreastfeeding = Dictionary.getConcept(Dictionary.NO);
		Concept unknown = Dictionary.getConcept(Dictionary.UNKNOWN);

		// give patient #7 an exclusive feeding method
		TestUtils.saveObs(TestUtils.getPatient(7), motherOnTreatmentAndBreatFeeding, breastfeeding,TestUtils.date(2012,6,10));
		// give patient #6 a replacement feeding method
		TestUtils.saveObs(TestUtils.getPatient(6), motherOnTreatmentAndBreatFeeding, notBreastfeeding,TestUtils.date(2012,6,15));
		// give patient #8 a mixed feeding option
		TestUtils.saveObs(TestUtils.getPatient(8), motherOnTreatmentAndBreatFeeding, unknown,TestUtils.date(2012,6,20));
		CohortDefinition cd = mchcsCohortLibrary.motherOnTreatmentAndBreastFeeding();
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(7), evaluated);
	}

	/**
	 * @see MchcsCohortLibrary#motherOnTreatmentAndNotBreastFeeding()
	 */
	@Test
	public void motherOnTreatmentAndNotBreastFeeding_shouldReturnMotherOnTreatmentAndNotBreastFeeding() throws Exception {
		Concept motherOnTreatmentAndBreatFeeding = Dictionary.getConcept(Dictionary.MOTHER_ON_ANTIRETROVIRAL_DRUGS_AND_BREASTFEEDING);
		Concept breastfeeding = Dictionary.getConcept(Dictionary.YES);
		Concept notBreastfeeding = Dictionary.getConcept(Dictionary.NO);
		Concept unknown = Dictionary.getConcept(Dictionary.UNKNOWN);

		// give patient #7 an exclusive feeding method
		TestUtils.saveObs(TestUtils.getPatient(7), motherOnTreatmentAndBreatFeeding, breastfeeding,TestUtils.date(2012,6,10));
		// give patient #6 a replacement feeding method
		TestUtils.saveObs(TestUtils.getPatient(6), motherOnTreatmentAndBreatFeeding, notBreastfeeding,TestUtils.date(2012,6,15));
		// give patient #8 a mixed feeding option
		TestUtils.saveObs(TestUtils.getPatient(8), motherOnTreatmentAndBreatFeeding, unknown,TestUtils.date(2012,6,20));
		CohortDefinition cd = mchcsCohortLibrary.motherOnTreatmentAndNotBreastFeeding();
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}

	/**
	 * @see MchcsCohortLibrary#motherOnTreatmentAndNotBreastFeedingUnknown()
	 */
	@Test
	public void motherOnTreatmentAndNotBreastFeedingUnknown_shouldReturnMotherOnTreatmentAndNotBreastFeedingUnknown() throws Exception{
		Concept motherOnTreatmentAndBreatFeeding = Dictionary.getConcept(Dictionary.MOTHER_ON_ANTIRETROVIRAL_DRUGS_AND_BREASTFEEDING);
		Concept breastfeeding = Dictionary.getConcept(Dictionary.YES);
		Concept notBreastfeeding = Dictionary.getConcept(Dictionary.NO);
		Concept unknown = Dictionary.getConcept(Dictionary.UNKNOWN);

		// give patient #7 an exclusive feeding method
		TestUtils.saveObs(TestUtils.getPatient(7), motherOnTreatmentAndBreatFeeding, breastfeeding,TestUtils.date(2012,6,10));
		// give patient #6 a replacement feeding method
		TestUtils.saveObs(TestUtils.getPatient(6), motherOnTreatmentAndBreatFeeding, notBreastfeeding,TestUtils.date(2012,6,15));
		// give patient #8 a mixed feeding option
		TestUtils.saveObs(TestUtils.getPatient(8), motherOnTreatmentAndBreatFeeding, unknown,TestUtils.date(2012,6,20));
		CohortDefinition cd = mchcsCohortLibrary.motherOnTreatmentAndNotBreastFeedingUnknown();
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(8), evaluated);
	}

	/**
	 * @see MchcsCohortLibrary#hivExposedInfants()
	 */
	@Test
	public void hivExposedInfants_shouldReturnHivExposedInfants() throws Exception {
		Concept childHivStatus = Dictionary.getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS);
		Concept hivExposed = Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV);
		Concept negative = Dictionary.getConcept(Dictionary.NEGATIVE);

		//make #6 hiv exposed status
		TestUtils.saveObs(TestUtils.getPatient(6), childHivStatus, hivExposed,TestUtils.date(2007, 7, 1));
		//make #7 hiv exposed but negative status
		TestUtils.saveObs(TestUtils.getPatient(7), childHivStatus, negative,TestUtils.date(2007, 7, 1));

		CohortDefinition cd = mchcsCohortLibrary.hivExposedInfants();
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);

	}

	/**
	 * @see MchcsCohortLibrary#hivExposedInfantsWithin2Months()
	 */
	@Test
	public void hivExposedInfantsWithin2Months_shouldReturnHivExposedInfantsWithin2Months() throws Exception{
		Concept childHivStatus = Dictionary.getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS);
		Concept hivExposed = Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV);
		Concept negative = Dictionary.getConcept(Dictionary.NEGATIVE);

		//make #6 hiv exposed status
		TestUtils.saveObs(TestUtils.getPatient(6), childHivStatus, hivExposed,TestUtils.date(2007, 7, 1));
		//make #7 hiv exposed but negative status
		TestUtils.saveObs(TestUtils.getPatient(7), childHivStatus, negative,TestUtils.date(2007, 7, 1));

		CohortDefinition cd = mchcsCohortLibrary.hivExposedInfantsWithin2Months();
		context.addParameterValue("onOrAfter", TestUtils.date(2007, 6, 1));
		context.addParameterValue("onOrBefore", TestUtils.date(2007, 7, 1));
		EvaluatedCohort evaluated = Context.getService(CohortDefinitionService.class).evaluate(cd, context);
		ReportingTestUtils.assertCohortEquals(Arrays.asList(6), evaluated);
	}

}
