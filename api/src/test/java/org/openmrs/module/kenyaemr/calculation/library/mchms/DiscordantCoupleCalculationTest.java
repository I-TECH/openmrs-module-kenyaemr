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
 * Tests for {@link org.openmrs.module.kenyaemr.calculation.library.mchms.DiscordantCoupleCalculation}
 */
public class DiscordantCoupleCalculationTest extends BaseModuleContextSensitiveTest {

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
	 * @verifies determine whether MCH-MS patients have HIV results discordant with those of their partners
	 * @see DiscordantCoupleCalculation#evaluate (
	 *      java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldDetermineWhetherCoupleIsDiscordant0() throws Exception {
		// Get the MCH-MS program, enrollment and consultation encounter types and enrollment and delivery forms
		Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);
		EncounterType enrollmentEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT);
		Form enrollmentForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_ENROLLMENT);

		//Prepare dates to use for testing
		Date enrollmentDate = TestUtils.date(2012, 1, 1);

		//Enroll  #2, #6, #7 and #8 into MCH-MS
		TestUtils.enrollInProgram(TestUtils.getPatient(2), mchmsProgram, enrollmentDate);
		for (int i = 6; i <= 8; i++) {
			TestUtils.enrollInProgram(TestUtils.getPatient(i), mchmsProgram, enrollmentDate);
		}

		//Get the HIV status, HIV test date concepts for both patient and her partner
		Concept patientHivStatusConcept = Dictionary.getConcept(Dictionary.HIV_STATUS);
		Concept partnerHivStatusConcept = Dictionary.getConcept(Dictionary.PARTNER_HIV_STATUS);
		Concept patientHivTestDateConcept = Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS);
		Concept partnerHivTestDateConcept = Dictionary.getConcept(Dictionary.DATE_OF_PARTNER_HIV_DIAGNOSIS);

		//Enroll all patients on {@enrollmentDate}

		//Enroll Pat#2 into MCH, patient is HIV positive, date of patient test {@enrollmentDate}
		{
			Patient patient = TestUtils.getPatient(2);
			Obs[] enrollmentEncounterObs = {TestUtils.saveObs(patient, patientHivStatusConcept, Dictionary.getConcept(Dictionary.POSITIVE), enrollmentDate)};
			TestUtils.saveEncounter(patient, enrollmentEncounterType, enrollmentForm, enrollmentDate, enrollmentEncounterObs);
			TestUtils.saveObs(patient, patientHivTestDateConcept, enrollmentDate, enrollmentDate);
		}

		//Enroll Pat#6 into MCH, patient's partner is HIV positive, date of patient partner's test {@enrollmentDate}
		{
			Patient patient = TestUtils.getPatient(6);
			Obs[] enrollmentEncounterObs = {TestUtils.saveObs(patient, partnerHivStatusConcept, Dictionary.getConcept(Dictionary.POSITIVE), enrollmentDate)};
			TestUtils.saveEncounter(patient, enrollmentEncounterType, enrollmentForm, enrollmentDate, enrollmentEncounterObs);
			TestUtils.saveObs(patient, partnerHivTestDateConcept, enrollmentDate, enrollmentDate);
		}

		//Enroll Pat#7 into MCH, both patient and patient's partner are HIV positive, date of patient and patient partner's test is {@enrollmentDate}
		{
			Patient patient = TestUtils.getPatient(7);
			Obs[] enrollmentEncounterObs = {TestUtils.saveObs(patient, patientHivStatusConcept, Dictionary.getConcept(Dictionary.POSITIVE), enrollmentDate)};
			TestUtils.saveEncounter(patient, enrollmentEncounterType, enrollmentForm, enrollmentDate, enrollmentEncounterObs);
			TestUtils.saveObs(patient, partnerHivStatusConcept, Dictionary.getConcept(Dictionary.POSITIVE), enrollmentDate);
			TestUtils.saveObs(patient, patientHivTestDateConcept, enrollmentDate, enrollmentDate);
			TestUtils.saveObs(patient, partnerHivTestDateConcept, enrollmentDate, enrollmentDate);
		}

		//Enroll Pat#8 into MCH, patient is HIV positive and patient's partner is HIV negative, date of patient and patient partner's test is {@enrollmentDate}
		{
			Patient patient = TestUtils.getPatient(8);
			Obs[] enrollmentEncounterObs = {TestUtils.saveObs(patient, patientHivStatusConcept, Dictionary.getConcept(Dictionary.POSITIVE), enrollmentDate)};
			TestUtils.saveEncounter(patient, enrollmentEncounterType, enrollmentForm, enrollmentDate, enrollmentEncounterObs);
			TestUtils.saveObs(patient, partnerHivStatusConcept, Dictionary.getConcept(Dictionary.NEGATIVE), enrollmentDate);
			TestUtils.saveObs(patient, patientHivTestDateConcept, enrollmentDate, enrollmentDate);
			TestUtils.saveObs(patient, partnerHivTestDateConcept, enrollmentDate, enrollmentDate);
		}

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8);

		Map<String, Object> parameters = new HashMap<String, Object>();

		CalculationResultMap resultMap = new DiscordantCoupleCalculation().evaluate(ptIds, parameters,
				Context.getService(PatientCalculationService.class).createCalculationContext());

		Assert.assertFalse((Boolean) resultMap.get(2).getValue());  //Not discordant
		Assert.assertFalse((Boolean) resultMap.get(6).getValue());  //Not discordant
		Assert.assertFalse((Boolean) resultMap.get(7).getValue());  //Not discordant
		Assert.assertTrue((Boolean) resultMap.get(8).getValue());  //Discordant
	}

	/**
	 * @verifies determine whether MCH-MS patients have HIV results discordant with those of their partners
	 * @see DiscordantCoupleCalculation#evaluate (
	 *      java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldDetermineWhetherCoupleIsDiscordant1() throws Exception {
		// Get the MCH-MS program, enrollment and consultation encounter types and enrollment and delivery forms
		Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);
		EncounterType enrollmentEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT);
		Form enrollmentForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_ENROLLMENT);

		//Prepare dates to use for testing
		Date enrollmentDate = TestUtils.date(2012, 1, 1);

		//Enroll  #2, #6, #7 and #8 into MCH-MS
		TestUtils.enrollInProgram(TestUtils.getPatient(2), mchmsProgram, enrollmentDate);
		for (int i = 6; i <= 8; i++) {
			TestUtils.enrollInProgram(TestUtils.getPatient(i), mchmsProgram, enrollmentDate);
		}

		//Get the HIV status, HIV test date concepts for both patient and her partner
		Concept patientHivStatusConcept = Dictionary.getConcept(Dictionary.HIV_STATUS);
		Concept partnerHivStatusConcept = Dictionary.getConcept(Dictionary.PARTNER_HIV_STATUS);
		Concept patientHivTestDateConcept = Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS);
		Concept partnerHivTestDateConcept = Dictionary.getConcept(Dictionary.DATE_OF_PARTNER_HIV_DIAGNOSIS);

		//Enroll all patients on {@enrollmentDate}

		//Enroll Pat#6 into MCH, patient's partner has unknown HIV status, date of patient partner's test {@enrollmentDate}
		{
			Patient patient = TestUtils.getPatient(6);
			Obs[] enrollmentEncounterObs = {TestUtils.saveObs(patient, partnerHivStatusConcept, Dictionary.getConcept(Dictionary.UNKNOWN), enrollmentDate)};
			TestUtils.saveEncounter(patient, enrollmentEncounterType, enrollmentForm, enrollmentDate, enrollmentEncounterObs);
			TestUtils.saveObs(patient, partnerHivTestDateConcept, enrollmentDate, enrollmentDate);
		}

		//Enroll Pat#7 into MCH, both patient and patient's partner are HIV negative, date of patient and patient partner's test is {@enrollmentDate}
		{
			Patient patient = TestUtils.getPatient(7);
			Obs[] enrollmentEncounterObs = {TestUtils.saveObs(patient, patientHivStatusConcept, Dictionary.getConcept(Dictionary.NEGATIVE), enrollmentDate)};
			TestUtils.saveEncounter(patient, enrollmentEncounterType, enrollmentForm, enrollmentDate, enrollmentEncounterObs);
			TestUtils.saveObs(patient, partnerHivStatusConcept, Dictionary.getConcept(Dictionary.NEGATIVE), enrollmentDate);
			TestUtils.saveObs(patient, patientHivTestDateConcept, enrollmentDate, enrollmentDate);
			TestUtils.saveObs(patient, partnerHivTestDateConcept, enrollmentDate, enrollmentDate);
		}

		//Enroll Pat#8 into MCH, patient is HIV negative and patient's partner is HIV positive, date of patient and patient partner's test is {@enrollmentDate}
		{
			Patient patient = TestUtils.getPatient(8);
			Obs[] enrollmentEncounterObs = {TestUtils.saveObs(patient, patientHivStatusConcept, Dictionary.getConcept(Dictionary.NEGATIVE), enrollmentDate)};
			TestUtils.saveEncounter(patient, enrollmentEncounterType, enrollmentForm, enrollmentDate, enrollmentEncounterObs);
			TestUtils.saveObs(patient, partnerHivStatusConcept, Dictionary.getConcept(Dictionary.POSITIVE), enrollmentDate);
			TestUtils.saveObs(patient, patientHivTestDateConcept, enrollmentDate, enrollmentDate);
			TestUtils.saveObs(patient, partnerHivTestDateConcept, enrollmentDate, enrollmentDate);
		}

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8);

		Map<String, Object> parameters = new HashMap<String, Object>();

		CalculationResultMap resultMap = new DiscordantCoupleCalculation().evaluate(ptIds, parameters,
				Context.getService(PatientCalculationService.class).createCalculationContext());

		Assert.assertFalse((Boolean) resultMap.get(2).getValue());  //Not enrolled
		Assert.assertFalse((Boolean) resultMap.get(6).getValue());  //No HIV results for patient nor for patient's partner
		Assert.assertFalse((Boolean) resultMap.get(7).getValue());  //Not discordant
		Assert.assertTrue((Boolean) resultMap.get(8).getValue());  //Discordant
	}
}