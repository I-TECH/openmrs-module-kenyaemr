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

package org.openmrs.module.kenyaemr.calculation.library.mchms;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.calculation.library.mchms.AssessedOnFirstVisitCalculation}
 */
public class AssessedOnFirstVisitCalculationTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;
	@Autowired
	private MchMetadata mchMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		mchMetadata.install();
	}

	/**
	 * @verifies determine whether MCH-MS patients were assessed for ART eligibility at first ANC visit
	 * @see AssessedOnFirstVisitCalculation#evaluate (
	 *      java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsWereAssessedOnFirstVisit() throws Exception {
		// Get the MCH-MS program, enrollment and consultation encounter types and enrollment and delivery forms
		Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);
		EncounterType enrollmentEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT);
		EncounterType consultationEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION);
		Form enrollmentForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_ENROLLMENT);
		Form antenatalVisitForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_ANTENATAL_VISIT);

		//Prepare dates to use for testing
		Date enrollmentDate = TestUtils.date(2012, 1, 1);

		//Enroll  #2, #6, #7 and #8 into MCH-MS
		TestUtils.enrollInProgram(TestUtils.getPatient(2), mchmsProgram, enrollmentDate);
		for (int i = 6; i <= 8; i++) {
			TestUtils.enrollInProgram(TestUtils.getPatient(i), mchmsProgram, enrollmentDate);
		}

		//Get the HIV status, WHO stage and CD4 count concepts
		Concept hivStatusConcept = Dictionary.getConcept(Dictionary.HIV_STATUS);
		Concept whoStageConcept = Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE);
		Concept cd4CountConcept = Dictionary.getConcept(Dictionary.CD4_COUNT);

//		Enroll all patients on {@enrollmentDate} and fill their delivery forms on {@deliveryDate}

		//Ennroll Pat#2 into MCH and create an ANC encounter with both WHO staging and CD4 count
		{
			Patient patient = TestUtils.getPatient(2);
			Obs[] enrollmentEncounterObs = {TestUtils.saveObs(patient, hivStatusConcept, Dictionary.getConcept(Dictionary.POSITIVE), enrollmentDate)};
			TestUtils.saveEncounter(patient, enrollmentEncounterType, enrollmentForm, enrollmentDate, enrollmentEncounterObs);
			Obs[] whoStagingAndCd4Obss = {TestUtils.saveObs(patient, cd4CountConcept, 500.00, enrollmentDate),
					TestUtils.saveObs(patient, whoStageConcept, Dictionary.getConcept(Dictionary.WHO_STAGE_1_ADULT), enrollmentDate)};
			TestUtils.saveEncounter(patient, consultationEncounterType, antenatalVisitForm, enrollmentDate, whoStagingAndCd4Obss);
		}

		//Ennroll Pat#6 into MCH and create an ANC encounter with CD4 count
		{
			Patient patient = TestUtils.getPatient(6);
			Obs[] enrollmentEncounterObs = {TestUtils.saveObs(patient, hivStatusConcept, Dictionary.getConcept(Dictionary.POSITIVE), enrollmentDate)};
			TestUtils.saveEncounter(patient, enrollmentEncounterType, enrollmentForm, enrollmentDate, enrollmentEncounterObs);
			Obs[] cd4Obs = {TestUtils.saveObs(patient, cd4CountConcept, 500.00, enrollmentDate)};
			TestUtils.saveEncounter(patient, consultationEncounterType, antenatalVisitForm, enrollmentDate, cd4Obs);
		}

		//Ennroll Pat#7 into MCH and create an ANC encounter with WHO staging
		{
			Patient patient = TestUtils.getPatient(7);
			Obs[] enrollmentEncounterObs = {TestUtils.saveObs(patient, hivStatusConcept, Dictionary.getConcept(Dictionary.POSITIVE), enrollmentDate)};
			TestUtils.saveEncounter(patient, enrollmentEncounterType, enrollmentForm, enrollmentDate, enrollmentEncounterObs);
			Obs[] whoStageObs = {TestUtils.saveObs(patient, whoStageConcept, Dictionary.getConcept(Dictionary.WHO_STAGE_1_ADULT), enrollmentDate)};
			TestUtils.saveEncounter(patient, consultationEncounterType, antenatalVisitForm, enrollmentDate, whoStageObs);
		}

		//Ennroll Pat#8 into MCH but don't create an ANC
		{
			Patient patient = TestUtils.getPatient(8);
			Obs[] enrollmentEncounterObs = {TestUtils.saveObs(patient, hivStatusConcept, Dictionary.getConcept(Dictionary.POSITIVE), enrollmentDate)};
			TestUtils.saveEncounter(patient, enrollmentEncounterType, enrollmentForm, enrollmentDate, enrollmentEncounterObs);
		}

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8);

		Map<String, Object> parameters = new HashMap<String, Object>();

		CalculationResultMap resultMap = new AssessedOnFirstVisitCalculation().evaluate(ptIds, parameters,
				Context.getService(PatientCalculationService.class).createCalculationContext());

		Assert.assertTrue((Boolean) resultMap.get(2).getValue());  //First ANC visit has both WHO stage and CD4 count obs
		Assert.assertTrue((Boolean) resultMap.get(6).getValue());  //First ANC visit has WHO stage obs
		Assert.assertTrue((Boolean) resultMap.get(7).getValue());  //First ANC visit has CD4 count obs
		Assert.assertFalse((Boolean) resultMap.get(8).getValue());  //No ANC Visit
	}
}