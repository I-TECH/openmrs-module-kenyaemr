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
 * Tests for {@link PartnerHivStatusUnknownCalculation}
 */
public class PartnerHivStatusUnknownCalculationTest extends BaseModuleContextSensitiveTest {

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
	 * @verifies determine whether MCH-MS patients' partners HIV status is unknown
	 * @see PartnerHivStatusUnknownCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsPartnersHiVStatusesAreUnknown() throws Exception {

		// Get the MCH-MS program, enrollment encounter type and enrollment form
		Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);
		EncounterType enrollmentEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT);
		Form enrollmentForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_ENROLLMENT);

		//Enroll  #6, #7 and #8 into MCH-MS
		for (int i = 6; i <= 8; i++) {
			TestUtils.enrollInProgram(TestUtils.getPatient(i), mchmsProgram, new Date());
		}

		//Get the Partner HIV Status concept
		Concept partnerHivStatus = Dictionary.getConcept(Dictionary.PARTNER_HIV_STATUS);

		//Create enrollment encounter for Pat#6 indicating partner HIV status unknown
		Obs[] encounterObss6 = {TestUtils.saveObs(TestUtils.getPatient(6), partnerHivStatus, Dictionary.getConcept(Dictionary.UNKNOWN), new Date())};
		TestUtils.saveEncounter(TestUtils.getPatient(6), enrollmentEncounterType, enrollmentForm, new Date(), encounterObss6);

		//Create enrollment encounter for Pat#7 indicating partner HIV status -ve
		Obs[] encounterObss7 = {TestUtils.saveObs(TestUtils.getPatient(7), partnerHivStatus, Dictionary.getConcept(Dictionary.NEGATIVE), new Date())};
		TestUtils.saveEncounter(TestUtils.getPatient(7), enrollmentEncounterType, enrollmentForm, new Date(), encounterObss7);

		//Create enrollment encounter for Pat#8 indicating partner HIV status +ve
		Obs[] encounterObss8 = {TestUtils.saveObs(TestUtils.getPatient(8), partnerHivStatus, Dictionary.getConcept(Dictionary.POSITIVE), new Date())};
		TestUtils.saveEncounter(TestUtils.getPatient(8), enrollmentEncounterType, enrollmentForm, new Date(), encounterObss8);

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(6, 7, 8, 2, 999);


		//Run UnKnownPartnerHivStatusCalculation with these test patients
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new PartnerHivStatusUnknownCalculation());

		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); //Unknown Partner HIV Status
		Assert.assertFalse((Boolean) resultMap.get(7).getValue()); //Known Partner HIV Status
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); //Known Partner HIV Status
		Assert.assertFalse((Boolean) resultMap.get(2).getValue()); // Not in MCH-MS Program
		Assert.assertFalse((Boolean) resultMap.get(999).getValue());  //Voided
	}
}