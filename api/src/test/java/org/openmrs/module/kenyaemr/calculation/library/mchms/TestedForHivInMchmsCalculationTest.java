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

import org.joda.time.DateTime;
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
import org.openmrs.module.kenyaemr.PregnancyStage;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests for {@link TestedForHivInMchmsCalculation}
 */
public class TestedForHivInMchmsCalculationTest extends BaseModuleContextSensitiveTest {

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
	 * @verifies determine whether MCH-MS patients have been tested for HIV
	 * @see org.openmrs.module.kenyaemr.calculation.library.mchms.TestedForHivInMchmsCalculation#evaluate (
	 *      java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsWasTestedForHivInMchms0() throws Exception {
		// Get the MCH-MS program, enrollment and consultation encounter types and enrollment and delivery forms
		Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);
		EncounterType enrollmentEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT);
		EncounterType consultationEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION);
		Form enrollmentForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_ENROLLMENT);
		Form deliveryForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_DELIVERY);

		//Prepare dates to use for testing
		Date enrollmentDate = TestUtils.date(2012, 1, 1);
		Date deliveryDate = TestUtils.date(2012, 10, 1);
		Date beforeEnrollmentDate = new DateTime(enrollmentDate).plusDays(-10).toDate();
		Date duringAntenatalDate = new DateTime(enrollmentDate).plusDays(10).toDate();
		Date duringDelivery = new DateTime(deliveryDate).plusDays(-1).toDate();
		Date duringPostnatalDate = new DateTime(deliveryDate).plusDays(2).toDate();

		//Enroll  #2, #6, #7 and #8 into MCH-MS
		TestUtils.enrollInProgram(TestUtils.getPatient(2), mchmsProgram, enrollmentDate);
		for (int i = 6; i <= 8; i++) {
			TestUtils.enrollInProgram(TestUtils.getPatient(i), mchmsProgram, enrollmentDate);
		}

		//Get the HIV status, HIV test date and delivery date concepts
		Concept hivStatusConcept = Dictionary.getConcept(Dictionary.HIV_STATUS);
		Concept hivTestDateConcept = Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS);
		Concept deliveryDateConcept = Dictionary.getConcept(Dictionary.DATE_OF_CONFINEMENT);

		//Enroll all patients on {@enrollmentDate} and fill their delivery forms on {@deliveryDate}

		//Ennroll Pat#2 into MCH, HIV positive, date of test {@beforeEnrollmentDate}
		{
			Patient patient = TestUtils.getPatient(2);
			Obs[] enrollmentEncounterObs = {TestUtils.saveObs(patient, hivStatusConcept, Dictionary.getConcept(Dictionary.POSITIVE), enrollmentDate)};
			TestUtils.saveEncounter(patient, enrollmentEncounterType, enrollmentForm, enrollmentDate, enrollmentEncounterObs);
			TestUtils.saveObs(patient, hivTestDateConcept, beforeEnrollmentDate, enrollmentDate);
			Obs[] deliveryEncounterObs = {TestUtils.saveObs(patient, deliveryDateConcept, deliveryDate, deliveryDate)};
			TestUtils.saveEncounter(patient, consultationEncounterType, deliveryForm, deliveryDate, deliveryEncounterObs);
		}

		//Ennroll Pat#6 into MCH, HIV positive, date of test {@duringAntenatalDate}
		{
			Patient patient = TestUtils.getPatient(6);
			Obs[] enrollmentEncounterObs = {TestUtils.saveObs(patient, hivStatusConcept, Dictionary.getConcept(Dictionary.POSITIVE), enrollmentDate)};
			TestUtils.saveEncounter(patient, enrollmentEncounterType, enrollmentForm, enrollmentDate, enrollmentEncounterObs);
			TestUtils.saveObs(patient, hivTestDateConcept, duringAntenatalDate, enrollmentDate);
			Obs[] deliveryEncounterObs = {TestUtils.saveObs(patient, deliveryDateConcept, deliveryDate, deliveryDate)};
			TestUtils.saveEncounter(patient, consultationEncounterType, deliveryForm, deliveryDate, deliveryEncounterObs);
		}

		//Ennroll Pat#7 into MCH, HIV positive, date of test {@duringDelivery}
		{
			Patient patient = TestUtils.getPatient(7);
			Obs[] enrollmentEncounterObs = {TestUtils.saveObs(patient, hivStatusConcept, Dictionary.getConcept(Dictionary.POSITIVE), enrollmentDate)};
			TestUtils.saveEncounter(patient, enrollmentEncounterType, enrollmentForm, enrollmentDate, enrollmentEncounterObs);
			TestUtils.saveObs(patient, hivTestDateConcept, duringDelivery, enrollmentDate);
			Obs[] deliveryEncounterObs = {TestUtils.saveObs(patient, deliveryDateConcept, deliveryDate, deliveryDate)};
			TestUtils.saveEncounter(patient, consultationEncounterType, deliveryForm, deliveryDate, deliveryEncounterObs);
		}

		//Ennroll Pat#8 into MCH, HIV positive, date of test {@duringPostnatalDate}
		{
			Patient patient = TestUtils.getPatient(8);
			Obs[] enrollmentEncounterObs = {TestUtils.saveObs(patient, hivStatusConcept, Dictionary.getConcept(Dictionary.POSITIVE), enrollmentDate)};
			TestUtils.saveEncounter(patient, enrollmentEncounterType, enrollmentForm, enrollmentDate, enrollmentEncounterObs);
			TestUtils.saveObs(patient, hivTestDateConcept, duringPostnatalDate, enrollmentDate);
			Obs[] deliveryEncounterObs = {TestUtils.saveObs(patient, deliveryDateConcept, deliveryDate, deliveryDate)};
			TestUtils.saveEncounter(patient, consultationEncounterType, deliveryForm, deliveryDate, deliveryEncounterObs);
		}

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8);

		//Test with paramaters stage=MchMetadata.PregnancyStage.AFTER_ENROLLMENT and result = null. Indicator (HV2-01).
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("stage", PregnancyStage.ANTENATAL);
			CalculationResultMap resultMap = new TestedForHivInMchmsCalculation().evaluate(ptIds, parameters,
					Context.getService(PatientCalculationService.class).createCalculationContext());

			Assert.assertFalse((Boolean) resultMap.get(2).getValue());  //HIV status known before enrollment
			Assert.assertTrue((Boolean) resultMap.get(6).getValue());  //HIV status known during antenatal stage
			Assert.assertFalse((Boolean) resultMap.get(7).getValue());  //HIV status known during delivery stage
			Assert.assertFalse((Boolean) resultMap.get(8).getValue());  //HIV status known during postnatal stage
		}

		//Test with paramaters stage=MchMetadata.PregnancyStage.DELIVERY and result = null. Indicator (HV2-02).
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("stage", PregnancyStage.DELIVERY);
			CalculationResultMap resultMap = new TestedForHivInMchmsCalculation().evaluate(ptIds, parameters,
					Context.getService(PatientCalculationService.class).createCalculationContext());

			Assert.assertFalse((Boolean) resultMap.get(2).getValue());  //HIV status known before enrollment
			Assert.assertFalse((Boolean) resultMap.get(6).getValue());  //HIV status known during antenatal stage
			Assert.assertTrue((Boolean) resultMap.get(7).getValue());  //HIV status known during delivery stage
			Assert.assertFalse((Boolean) resultMap.get(8).getValue());  //HIV status known during postnatal stage
		}

		//Test with paramaters stage=MchMetadata.PregnancyStage.POSTNATAL and result = null. Indicator (HV2-03).
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("stage", PregnancyStage.POSTNATAL);
			CalculationResultMap resultMap = new TestedForHivInMchmsCalculation().evaluate(ptIds, parameters,
					Context.getService(PatientCalculationService.class).createCalculationContext());

			Assert.assertFalse((Boolean) resultMap.get(2).getValue());  //HIV status known before enrollment
			Assert.assertFalse((Boolean) resultMap.get(6).getValue());  //HIV status known during antenatal stage
			Assert.assertFalse((Boolean) resultMap.get(7).getValue());  //HIV status known during delivery stage
			Assert.assertTrue((Boolean) resultMap.get(8).getValue());  //HIV status known during postnatal stage
		}

		//Test with paramaters stage=MchMetadata.PregnancyStage.AFTER_ENROLLMENT and result = null. Indicator (HV2-04).
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("stage", PregnancyStage.AFTER_ENROLLMENT);
			CalculationResultMap resultMap = new TestedForHivInMchmsCalculation().evaluate(ptIds, parameters,
					Context.getService(PatientCalculationService.class).createCalculationContext());

			Assert.assertFalse((Boolean) resultMap.get(2).getValue());  //HIV status known before enrollment
			Assert.assertTrue((Boolean) resultMap.get(6).getValue());  //HIV status known during antenatal stage
			Assert.assertTrue((Boolean) resultMap.get(7).getValue());  //HIV status known during delivery stage
			Assert.assertTrue((Boolean) resultMap.get(8).getValue());  //HIV status known during postnatal stage
		}

		//Test with paramaters stage=MchMetadata.PregnancyStage.BEFORE_ENROLLMENT and result = positive. Indicator (HV2-05).
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("stage", PregnancyStage.BEFORE_ENROLLMENT);
			parameters.put("result", Dictionary.getConcept(Dictionary.POSITIVE));
			CalculationResultMap resultMap = new TestedForHivInMchmsCalculation().evaluate(ptIds, parameters,
					Context.getService(PatientCalculationService.class).createCalculationContext());

			Assert.assertTrue((Boolean) resultMap.get(2).getValue());  //HIV positive status known before enrollment
			Assert.assertFalse((Boolean) resultMap.get(6).getValue());  //HIV positive status known during antenatal stage
			Assert.assertFalse((Boolean) resultMap.get(7).getValue());  //HIV positive status known during delivery stage
			Assert.assertFalse((Boolean) resultMap.get(8).getValue());  //HIV positive status known during postnatal stage
		}

		//Test with paramaters stage=MchMetadata.PregnancyStage.ANTENATAL and result = positive. Indicator (HV2-06).
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("stage", PregnancyStage.ANTENATAL);
			parameters.put("result", Dictionary.getConcept(Dictionary.POSITIVE));
			CalculationResultMap resultMap = new TestedForHivInMchmsCalculation().evaluate(ptIds, parameters,
					Context.getService(PatientCalculationService.class).createCalculationContext());

			Assert.assertFalse((Boolean) resultMap.get(2).getValue());  //HIV positive status known before enrollment
			Assert.assertTrue((Boolean) resultMap.get(6).getValue());  //HIV positive status known during antenatal stage
			Assert.assertFalse((Boolean) resultMap.get(7).getValue());  //HIV positive status known during delivery stage
			Assert.assertFalse((Boolean) resultMap.get(8).getValue());  //HIV positive status known during postnatal stage
		}

		//Test with paramaters stage=MchMetadata.PregnancyStage.DELIVERY and result = positive. Indicator (HV2-04).
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("stage", PregnancyStage.DELIVERY);
			parameters.put("result", Dictionary.getConcept(Dictionary.POSITIVE));
			CalculationResultMap resultMap = new TestedForHivInMchmsCalculation().evaluate(ptIds, parameters,
					Context.getService(PatientCalculationService.class).createCalculationContext());

			Assert.assertFalse((Boolean) resultMap.get(2).getValue());  //HIV positive status known before enrollment
			Assert.assertFalse((Boolean) resultMap.get(6).getValue());  //HIV positive status known during antenatal stage
			Assert.assertTrue((Boolean) resultMap.get(7).getValue());  //HIV positive status known during delivery stage
			Assert.assertFalse((Boolean) resultMap.get(8).getValue());  //HIV positive status known during postnatal stage
		}

		//Test with paramaters stage=MchMetadata.PregnancyStage.POSTNATAL and result = positive. Indicator (HV2-04).
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("stage", PregnancyStage.POSTNATAL);
			parameters.put("result", Dictionary.getConcept(Dictionary.POSITIVE));
			CalculationResultMap resultMap = new TestedForHivInMchmsCalculation().evaluate(ptIds, parameters,
					Context.getService(PatientCalculationService.class).createCalculationContext());

			Assert.assertFalse((Boolean) resultMap.get(2).getValue());  //HIV positive status known before enrollment
			Assert.assertFalse((Boolean) resultMap.get(6).getValue());  //HIV positive status known during antenatal stage
			Assert.assertFalse((Boolean) resultMap.get(7).getValue());  //HIV positive status known during delivery stage
			Assert.assertTrue((Boolean) resultMap.get(8).getValue());  //HIV positive status known during postnatal stage
		}
	}

	/**
	 * @verifies determine whether MCH-MS patients have been tested for HIV
	 * @see org.openmrs.module.kenyaemr.calculation.library.mchms.TestedForHivInMchmsCalculation#evaluate (
	 *      java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsWasTestedForHivInMchms1() throws Exception {
		// Get the MCH-MS program, enrollment and consultation encounter types and enrollment and delivery forms
		Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);
		EncounterType enrollmentEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT);
		EncounterType consultationEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION);
		Form enrollmentForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_ENROLLMENT);
		Form deliveryForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_DELIVERY);

		//Prepare dates to use for testing
		Date enrollmentDate = TestUtils.date(2012, 1, 1);
		Date deliveryDate = TestUtils.date(2012, 10, 1);
		Date duringDelivery = new DateTime(deliveryDate).plusDays(-1).toDate();
		Date afterPostnatalDate = new DateTime(deliveryDate).plusDays(10).toDate();

		//Enroll  #6 and #7 into MCH-MS

		for (int i = 6; i <= 7; i++) {
			TestUtils.enrollInProgram(TestUtils.getPatient(i), mchmsProgram, enrollmentDate);
		}

		//Get the HIV status, HIV test date and delivery date concepts
		Concept hivStatusConcept = Dictionary.getConcept(Dictionary.HIV_STATUS);
		Concept hivTestDateConcept = Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS);
		Concept deliveryDateConcept = Dictionary.getConcept(Dictionary.DATE_OF_CONFINEMENT);

		//Enroll Pat#6 into MCH on {@enrollmentDate} and fill her delivery form on {@deliveryDate}, HIV positive, date of test {@afterPostnatalDate}
		{
			Patient patient = TestUtils.getPatient(6);
			Obs[] enrollmentEncounterObs = {TestUtils.saveObs(patient, hivStatusConcept, Dictionary.getConcept(Dictionary.POSITIVE), enrollmentDate)};
			TestUtils.saveEncounter(patient, enrollmentEncounterType, enrollmentForm, enrollmentDate, enrollmentEncounterObs);
			TestUtils.saveObs(patient, hivTestDateConcept, afterPostnatalDate, enrollmentDate);
			Obs[] deliveryEncounterObs = {TestUtils.saveObs(patient, deliveryDateConcept, deliveryDate, deliveryDate)};
			TestUtils.saveEncounter(patient, consultationEncounterType, deliveryForm, deliveryDate, deliveryEncounterObs);
		}

		//Enroll Pat#7 into MCH on {@enrollmentDate}, HIV positive, date of test {@duringDelivery}
		{
			Patient patient = TestUtils.getPatient(7);
			Obs[] enrollmentEncounterObs = {TestUtils.saveObs(patient, hivStatusConcept, Dictionary.getConcept(Dictionary.POSITIVE), enrollmentDate)};
			TestUtils.saveEncounter(patient, enrollmentEncounterType, enrollmentForm, enrollmentDate, enrollmentEncounterObs);
			TestUtils.saveObs(patient, hivTestDateConcept, duringDelivery, enrollmentDate);
		}

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(2, 6, 7, 999);

		//Test with paramaters stage=MchMetadata.PregnancyStage.AFTER_ENROLLMENT and result = null. Indicator (HV2-01).
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("stage", PregnancyStage.ANTENATAL);
			CalculationResultMap resultMap = new TestedForHivInMchmsCalculation().evaluate(ptIds, parameters,
					Context.getService(PatientCalculationService.class).createCalculationContext());

			Assert.assertFalse((Boolean) resultMap.get(2).getValue());  //Not in MCHMS program
			Assert.assertFalse((Boolean) resultMap.get(6).getValue());  //HIV status known after postnatal stage
			Assert.assertTrue((Boolean) resultMap.get(7).getValue());  //HIV status known before delivery (no delivery record)
			Assert.assertFalse((Boolean) resultMap.get(999).getValue());  //Voided patient
		}

		//Test with paramaters stage=MchMetadata.PregnancyStage.DELIVERY and result = null. Indicator (HV2-02).
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("stage", PregnancyStage.DELIVERY);
			CalculationResultMap resultMap = new TestedForHivInMchmsCalculation().evaluate(ptIds, parameters,
					Context.getService(PatientCalculationService.class).createCalculationContext());

			Assert.assertFalse((Boolean) resultMap.get(2).getValue());  //Not in MCHMS program
			Assert.assertFalse((Boolean) resultMap.get(6).getValue());  //HIV status known after postnatal stage
			Assert.assertFalse((Boolean) resultMap.get(7).getValue());  //HIV status known before delivery (no delivery record)
			Assert.assertFalse((Boolean) resultMap.get(999).getValue());  //Voided patient
		}

		//Test with paramaters stage=MchMetadata.PregnancyStage.POSTNATAL and result = null. Indicator (HV2-03).
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("stage", PregnancyStage.POSTNATAL);
			CalculationResultMap resultMap = new TestedForHivInMchmsCalculation().evaluate(ptIds, parameters,
					Context.getService(PatientCalculationService.class).createCalculationContext());

			Assert.assertFalse((Boolean) resultMap.get(2).getValue());  //Not in MCHMS program
			Assert.assertFalse((Boolean) resultMap.get(6).getValue());  //HIV status known after postnatal stage
			Assert.assertFalse((Boolean) resultMap.get(7).getValue());  //HIV status known before delivery (no delivery record)
			Assert.assertFalse((Boolean) resultMap.get(999).getValue());  //Voided patient
		}

		//Test with paramaters stage=MchMetadata.PregnancyStage.AFTER_ENROLLMENT and result = null. Indicator (HV2-04).
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("stage", PregnancyStage.AFTER_ENROLLMENT);
			CalculationResultMap resultMap = new TestedForHivInMchmsCalculation().evaluate(ptIds, parameters,
					Context.getService(PatientCalculationService.class).createCalculationContext());

			Assert.assertFalse((Boolean) resultMap.get(2).getValue());  //Not in MCHMS program
			Assert.assertFalse((Boolean) resultMap.get(6).getValue());  //HIV status known after postnatal stage
			Assert.assertTrue((Boolean) resultMap.get(7).getValue());  //HIV status known before delivery (no delivery record)
			Assert.assertFalse((Boolean) resultMap.get(999).getValue());  //Voided patient
		}

		//Test with paramaters stage=MchMetadata.PregnancyStage.BEFORE_ENROLLMENT and result = positive. Indicator (HV2-05).
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("stage", PregnancyStage.BEFORE_ENROLLMENT);
			parameters.put("result", Dictionary.getConcept(Dictionary.POSITIVE));
			CalculationResultMap resultMap = new TestedForHivInMchmsCalculation().evaluate(ptIds, parameters,
					Context.getService(PatientCalculationService.class).createCalculationContext());

			Assert.assertFalse((Boolean) resultMap.get(2).getValue());  //Not in MCHMS program
			Assert.assertFalse((Boolean) resultMap.get(6).getValue());  //HIV positive status known after postnatal stage
			Assert.assertFalse((Boolean) resultMap.get(7).getValue());  //HIV positive status known before delivery (no delivery record)
			Assert.assertFalse((Boolean) resultMap.get(999).getValue());  //Voided patient
		}

		//Test with paramaters stage=MchMetadata.PregnancyStage.ANTENATAL and result = positive. Indicator (HV2-06).
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("stage", PregnancyStage.ANTENATAL);
			parameters.put("result", Dictionary.getConcept(Dictionary.POSITIVE));
			CalculationResultMap resultMap = new TestedForHivInMchmsCalculation().evaluate(ptIds, parameters,
					Context.getService(PatientCalculationService.class).createCalculationContext());

			Assert.assertFalse((Boolean) resultMap.get(2).getValue());  //Not in MCHMS program
			Assert.assertFalse((Boolean) resultMap.get(6).getValue());  //HIV positive status known after postnatal stage
			Assert.assertTrue((Boolean) resultMap.get(7).getValue());  //HIV positive status known before delivery (no delivery record)
			Assert.assertFalse((Boolean) resultMap.get(999).getValue());  //Voided patient
		}

		//Test with paramaters stage=MchMetadata.PregnancyStage.DELIVERY and result = positive. Indicator (HV2-04).
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("stage", PregnancyStage.DELIVERY);
			parameters.put("result", Dictionary.getConcept(Dictionary.POSITIVE));
			CalculationResultMap resultMap = new TestedForHivInMchmsCalculation().evaluate(ptIds, parameters,
					Context.getService(PatientCalculationService.class).createCalculationContext());

			Assert.assertFalse((Boolean) resultMap.get(2).getValue());  //Not in MCHMS program
			Assert.assertFalse((Boolean) resultMap.get(6).getValue());  //HIV positive status known after postnatal stage
			Assert.assertFalse((Boolean) resultMap.get(7).getValue());  //HIV positive status known before delivery (no delivery record)
			Assert.assertFalse((Boolean) resultMap.get(999).getValue());  //Voided patient
		}

		//Test with paramaters stage=MchMetadata.PregnancyStage.POSTNATAL and result = positive. Indicator (HV2-04).
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("stage", PregnancyStage.POSTNATAL);
			parameters.put("result", Dictionary.getConcept(Dictionary.POSITIVE));
			CalculationResultMap resultMap = new TestedForHivInMchmsCalculation().evaluate(ptIds, parameters,
					Context.getService(PatientCalculationService.class).createCalculationContext());

			Assert.assertFalse((Boolean) resultMap.get(2).getValue());  //Not in MCHMS program
			Assert.assertFalse((Boolean) resultMap.get(6).getValue());  //HIV positive status known after postnatal stage
			Assert.assertFalse((Boolean) resultMap.get(7).getValue());  //HIV positive status known before delivery (no delivery record)
			Assert.assertFalse((Boolean) resultMap.get(999).getValue());  //Voided patient
		}
	}
}