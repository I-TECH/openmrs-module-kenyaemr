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
import java.util.List;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.calculation.library.mchms.OnHaartCalculation}
 */
public class OnHaartCalculationTest extends BaseModuleContextSensitiveTest {

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
	 * @see org.openmrs.module.kenyaemr.calculation.library.mchms.OnHaartCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsAreOnHaart() throws Exception {

		// Get the MCH-MS program, enrollment encounter type and enrollment form
		Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);
		EncounterType enrollmentEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT);
		Form enrollmentForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_ENROLLMENT);

		//Enroll  #2, #6, #7 and #8 into MCH-MS
		TestUtils.enrollInProgram(TestUtils.getPatient(2), mchmsProgram, new Date());
		for (int i = 6; i <= 8; i++) {
			TestUtils.enrollInProgram(TestUtils.getPatient(i), mchmsProgram, new Date());
		}

		//Get the HIV Status and the ART use in Pregnanacy concepts
		Concept hivStatus = Dictionary.getConcept(Dictionary.HIV_STATUS);
		Concept artUse = Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_USE_IN_PREGNANCY);

		//Create enrollment encounter for Pat#2 indicating HIV status -ve
		Obs[] encounterObss2 = {TestUtils.saveObs(TestUtils.getPatient(2), hivStatus, Dictionary.getConcept(Dictionary.NEGATIVE), new Date())};
		TestUtils.saveEncounter(TestUtils.getPatient(2), enrollmentEncounterType, enrollmentForm, new Date(), encounterObss2);
		//Create ART use in Pregnancy obs for Pat#2 indicating NOT_APPLICABLE
		TestUtils.saveObs(TestUtils.getPatient(2), artUse, Dictionary.getConcept(Dictionary.NOT_APPLICABLE), new Date());

		//Create enrollment encounter for Pat#6 indicating HIV status +ve
		Obs[] encounterObss6 = {TestUtils.saveObs(TestUtils.getPatient(6), hivStatus, Dictionary.getConcept(Dictionary.POSITIVE), new Date())};
		TestUtils.saveEncounter(TestUtils.getPatient(6), enrollmentEncounterType, enrollmentForm, new Date(), encounterObss6);
		//Create ART use in Pregnancy obs for Pat#6 indicating NOT_APPLICABLE
		TestUtils.saveObs(TestUtils.getPatient(6), artUse, Dictionary.getConcept(Dictionary.NOT_APPLICABLE), new Date());

		//Create enrollment encounter for Pat#7 indicating HIV status +ve
		Obs[] encounterObss7 = {TestUtils.saveObs(TestUtils.getPatient(7), hivStatus, Dictionary.getConcept(Dictionary.POSITIVE), new Date())};
		TestUtils.saveEncounter(TestUtils.getPatient(7), enrollmentEncounterType, enrollmentForm, new Date(), encounterObss7);
		//Create ART use in Pregnancy obs for Pat#8 indicating MOTHER_ON_HAART
		TestUtils.saveObs(TestUtils.getPatient(7), artUse, Dictionary.getConcept(Dictionary.MOTHER_ON_HAART), new Date());

		//Create enroollment encounter for Pat#8 indicating HIV status +ve
		Obs[] encounterObss8 = {TestUtils.saveObs(TestUtils.getPatient(8), hivStatus, Dictionary.getConcept(Dictionary.POSITIVE), new Date())};
		TestUtils.saveEncounter(TestUtils.getPatient(8), enrollmentEncounterType, enrollmentForm, new Date(), encounterObss8);
		//Create ART use in Pregnancy obs for Pat#8  indicating MOTHER_ON_PROPHYLAXIS
		TestUtils.saveObs(TestUtils.getPatient(8), artUse, Dictionary.getConcept(Dictionary.MOTHER_ON_PROPHYLAXIS), new Date());

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8);

		//Run OnHaartCalculation with these test patients
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new OnHaartCalculation());

		Assert.assertFalse((Boolean) resultMap.get(2).getValue());  //Not HIV+
		Assert.assertFalse((Boolean) resultMap.get(6).getValue()); //HIV+ and not on HAART
		Assert.assertTrue((Boolean) resultMap.get(7).getValue()); //HIV+ on HAART
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); //HIV+ and not on HAART
	}

	/**
	 * @see OnHaartCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies determine whether MCH-MS patients have been tested for HIV
	 */
	@Test
	public void evaluate2_shouldDetermineWhetherPatientsAreOnHaart() throws Exception {
		List<Integer> ptIds = Arrays.asList(2, 999);
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new OnHaartCalculation());

		Assert.assertFalse((Boolean) resultMap.get(2).getValue()); // Not in MCH-MS Program
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // Voided patient
	}
}