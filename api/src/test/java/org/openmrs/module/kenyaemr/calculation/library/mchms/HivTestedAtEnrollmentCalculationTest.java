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
 * Tests for {@link HivTestedAtEnrollmentCalculation}
 */
public class HivTestedAtEnrollmentCalculationTest extends BaseModuleContextSensitiveTest {

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
	 * @see org.openmrs.module.kenyaemr.calculation.library.mchms.HivTestedAtEnrollmentCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies determine whether MCH-MS patients had a known HIV status at enrollment
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientHadKnownHivStatusAtEnrollment() throws Exception {

		// Get the MCH-MS program, enrollment encounter type and enrollment form
		Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);
		EncounterType enrollmentEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT);
		Form enrollmentForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_ENROLLMENT);

		//Enroll  #2, #6, #7 and #8 into MCH-MS
		TestUtils.enrollInProgram(TestUtils.getPatient(2), mchmsProgram, new Date());
		for (int i = 6; i <= 8; i++) {
			TestUtils.enrollInProgram(TestUtils.getPatient(i), mchmsProgram, new Date());
		}

		//Get the HIV Status and the HIV Test Date concepts
		Concept hivStatus = Dictionary.getConcept(Dictionary.HIV_STATUS);
		Concept hivTestDate = Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS);

		//Prepare enrollment date and also different dates when HIV test was done (a week before enrollment and a week after enrollment)
		Calendar enrollmentDate = Calendar.getInstance();
		Calendar oneWeekBeforeEnrollment = Calendar.getInstance();
		oneWeekBeforeEnrollment.add(Calendar.DATE, -7);
		Calendar oneWeekAfterEnrollment = Calendar.getInstance();
		oneWeekAfterEnrollment.add(Calendar.DATE, 7);

		//Create enrollment encounter for Pat#2 indicating HIV status 'Not Tested' known before enrollment
		Obs[] encounterObss2 = {TestUtils.saveObs(TestUtils.getPatient(2), hivStatus, Dictionary.getConcept(Dictionary.NOT_HIV_TESTED), new Date()),
				TestUtils.saveObs(TestUtils.getPatient(2), hivTestDate, oneWeekBeforeEnrollment.getTime(), new Date())};
		TestUtils.saveEncounter(TestUtils.getPatient(2), enrollmentEncounterType,enrollmentForm, new Date(), encounterObss2);

		//Create enrollment encounter for Pat#6 indicating HIV status +ve known before enrollment
		Obs[] encounterObss6 = {TestUtils.saveObs(TestUtils.getPatient(6), hivStatus, Dictionary.getConcept(Dictionary.POSITIVE), new Date()),
				TestUtils.saveObs(TestUtils.getPatient(6), hivTestDate, oneWeekBeforeEnrollment.getTime(), new Date())};
		TestUtils.saveEncounter(TestUtils.getPatient(6), enrollmentEncounterType,enrollmentForm, new Date(), encounterObss6);

		//Create enrollment encounter for Pat#7 indicating HIV status +ve known at enrollment
		Obs[] encounterObss7 = {TestUtils.saveObs(TestUtils.getPatient(7), hivStatus, Dictionary.getConcept(Dictionary.POSITIVE), new Date()),
				TestUtils.saveObs(TestUtils.getPatient(7), hivTestDate, enrollmentDate.getTime(), new Date())};
		TestUtils.saveEncounter(TestUtils.getPatient(7), enrollmentEncounterType,enrollmentForm, new Date(), encounterObss7);

		//Create enrollment encounter for Pat#8 indicating HIV status +ve known after enrollment
		Obs[] encounterObss8 = {TestUtils.saveObs(TestUtils.getPatient(8), hivStatus, Dictionary.getConcept(Dictionary.POSITIVE), new Date()),
				TestUtils.saveObs(TestUtils.getPatient(8), hivTestDate, oneWeekAfterEnrollment.getTime(), new Date())};
		TestUtils.saveEncounter(TestUtils.getPatient(8), enrollmentEncounterType,enrollmentForm, new Date(), encounterObss8);

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8);

		//Run HivTestedAtEnrollmentCalculation with these test patients
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new HivTestedAtEnrollmentCalculation());

		Assert.assertFalse((Boolean) resultMap.get(2).getValue());  //HIV status not known
		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); //HIV status known before enrollment
		Assert.assertTrue((Boolean) resultMap.get(7).getValue()); //HIV status known at enrollment
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); //HIV status known after enrollment
	}

	/**
	 * @see HivTestedAtEnrollmentCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies determine whether MCH-MS patients have been tested for HIV
	 */
	@Test
	public void evaluate2_shouldDetermineWhetherPatientHadKnownHivStatusAtEnrollment() throws Exception {
		List<Integer> ptIds = Arrays.asList(2, 999);
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new HivTestedAtEnrollmentCalculation());
		Assert.assertFalse((Boolean) resultMap.get(2).getValue()); // Not in MCH-MS Program
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // Voided patient
	}
}
