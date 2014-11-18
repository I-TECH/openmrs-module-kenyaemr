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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Tests for {@link NotOnArtCalculation}
 */
public class NotOnArtCalculationTest extends BaseModuleContextSensitiveTest {

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
	 * @see NotOnArtCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies determine whether MCH-MS patients have been tested for HIV
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsAreNotOnArt() throws Exception {

		// Get the MCH-MS program, enrollment encounter type and enrollment form
		Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);
		EncounterType enrollmentEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT);
		Form enrollmentForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_ENROLLMENT);

		//Enroll  #2, #6, #7 and #8 into MCH-MS
		TestUtils.enrollInProgram(TestUtils.getPatient(2), mchmsProgram, new Date());
		for (int i = 6; i <= 8; i++) {
			TestUtils.enrollInProgram(TestUtils.getPatient(i), mchmsProgram, new Date());
		}

		//Get the HIV Status concept, the LMP concept and the ART use in Pregnanacy concepts
		Concept hivStatus = Dictionary.getConcept(Dictionary.HIV_STATUS);
		Concept lmp = Dictionary.getConcept(Dictionary.LAST_MONTHLY_PERIOD);
		Concept artUse = Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_USE_IN_PREGNANCY);

		//Prepare times since last LMP (10 weeks ago and 20 weeks ago)
		Calendar tenWeeksAgo = Calendar.getInstance();
		tenWeeksAgo.add(Calendar.DATE, -70);
		Calendar twentyWeeksAgo = Calendar.getInstance();
		twentyWeeksAgo.add(Calendar.DATE, -140);

		//Create enrollment encounter for Pat#2 indicating HIV status - and LMP 20 weeks ago
		Obs[] encounterObss2 = {TestUtils.saveObs(TestUtils.getPatient(2), hivStatus, Dictionary.getConcept(Dictionary.NEGATIVE), new Date()),
				TestUtils.saveObs(TestUtils.getPatient(2), lmp, twentyWeeksAgo.getTime(), new Date())};
		TestUtils.saveEncounter(TestUtils.getPatient(2), enrollmentEncounterType,enrollmentForm, new Date(), encounterObss2);
		//Create ART use in Pregnancy obs for Pat#2 indicating NOT_APPLICABLE
		TestUtils.saveObs(TestUtils.getPatient(2), artUse, Dictionary.getConcept(Dictionary.NOT_APPLICABLE), new Date());

		//Create enrollment encounter for Pat#6 indicating HIV status + and LMP 10 weeks ago
		Obs[] encounterObss6 = {TestUtils.saveObs(TestUtils.getPatient(6), hivStatus, Dictionary.getConcept(Dictionary.POSITIVE), new Date()),
				TestUtils.saveObs(TestUtils.getPatient(6), lmp, tenWeeksAgo.getTime(), new Date())};
		TestUtils.saveEncounter(TestUtils.getPatient(6), enrollmentEncounterType,enrollmentForm, new Date(), encounterObss6);
		//Create ART use in Pregnancy obs for Pat#6 indicating NOT_APPLICABLE
		TestUtils.saveObs(TestUtils.getPatient(6), artUse, Dictionary.getConcept(Dictionary.NOT_APPLICABLE), new Date());

		//Create enrollment encounter for Pat#7 indicating HIV status + and LMP 20 weeks ago
		Obs[] encounterObss7 = {TestUtils.saveObs(TestUtils.getPatient(7), hivStatus, Dictionary.getConcept(Dictionary.POSITIVE), new Date()),
				TestUtils.saveObs(TestUtils.getPatient(7), lmp, twentyWeeksAgo.getTime(), new Date())};
		TestUtils.saveEncounter(TestUtils.getPatient(7), enrollmentEncounterType,enrollmentForm, new Date(), encounterObss7);
		//Create ART use in Pregnancy obs for Pat#8 indicating NOT_APPLICABLE
		TestUtils.saveObs(TestUtils.getPatient(7), artUse, Dictionary.getConcept(Dictionary.NOT_APPLICABLE), new Date());

		//Create enroollment encounter for Pat#8 indicating HIV status + and LMP 20 weeks ago
		Obs[] encounterObss8 = {TestUtils.saveObs(TestUtils.getPatient(8), hivStatus, Dictionary.getConcept(Dictionary.POSITIVE), new Date()),
				TestUtils.saveObs(TestUtils.getPatient(8), lmp, twentyWeeksAgo.getTime(), new Date())};
		TestUtils.saveEncounter(TestUtils.getPatient(8), enrollmentEncounterType,enrollmentForm, new Date(), encounterObss8);
		//Create ART use in Pregnancy obs for Pat#8  indicating MOTHER_ON_PROPHYLAXIS
		TestUtils.saveObs(TestUtils.getPatient(8), artUse, Dictionary.getConcept(Dictionary.MOTHER_ON_PROPHYLAXIS), new Date());

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8);

		//Run NotOnArtCalculation with these test patients
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new NotOnArtCalculation());

		Assert.assertFalse((Boolean) resultMap.get(2).getValue());  //Not HIV+ - need not be on ART
		Assert.assertFalse((Boolean) resultMap.get(6).getValue()); //HIV+ and not on ART but gestation less than 14 weeks - need not be on ART
		Assert.assertTrue((Boolean) resultMap.get(7).getValue()); //HIV+ and not on ART and gestation is greater than 14 weeks - needs to be on ART
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); //HIV+ and on ART and gestation is greater than 14 weeks - need not to be on ART
	}

	/**
	 * @see NotOnArtCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies determine whether MCH-MS patients have been tested for HIV
	 */
	@Test
	public void evaluate2_shouldDetermineWhetherPatientsAreNotOnArt() throws Exception {
		List<Integer> ptIds = Arrays.asList(2, 999);
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new NotOnArtCalculation());
		Assert.assertFalse((Boolean) resultMap.get(2).getValue()); // Not in MCH-MS Program - need not be on ART
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // Voided patient
	}
}