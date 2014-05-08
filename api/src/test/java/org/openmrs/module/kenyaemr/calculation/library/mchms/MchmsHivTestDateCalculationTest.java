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
import java.util.List;

/**
 * Tests for {@link MchmsHivTestDateCalculation}
 */
public class MchmsHivTestDateCalculationTest extends BaseModuleContextSensitiveTest {

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
	 * @verifies determine when MCH-MS patients were tested for HIV
	 * @see MchmsHivTestDateCalculation#evaluate (
	 *      java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldDetermineWhenPatientWasTestedForHiv() throws Exception {
		// Get the MCH-MS program, enrollment and consultation encounter types and enrollment and delivery forms
		Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);
		EncounterType enrollmentEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_ENROLLMENT);
		EncounterType consultationEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHMS_CONSULTATION);
		Form enrollmentForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_ENROLLMENT);
		Form deliveryForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHMS_DELIVERY);

		//Prepare dates to use for testing
		Date enrollmentDate = TestUtils.date(2012, 1, 1);
		Date deliveryDate = TestUtils.date(2012, 10, 1);

		//Enroll #6 and #7 into MCH-MS
		for (int i = 6; i <= 7; i++) {
			TestUtils.enrollInProgram(TestUtils.getPatient(i), mchmsProgram, enrollmentDate);
		}

		//Get the HIV status, HIV test date and delivery date concepts
		Concept hivStatusConcept = Dictionary.getConcept(Dictionary.HIV_STATUS);
		Concept hivTestDateConcept = Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS);
		Concept deliveryDateConcept = Dictionary.getConcept(Dictionary.DATE_OF_CONFINEMENT);


		//Set Pat#6 HIV status to positive, no date of test
		{
			Patient patient = TestUtils.getPatient(6);
			Obs[] enrollmentEncounterObs = {TestUtils.saveObs(patient, hivStatusConcept, Dictionary.getConcept(Dictionary.POSITIVE), enrollmentDate)};
			TestUtils.saveEncounter(patient, enrollmentEncounterType, enrollmentForm, enrollmentDate, enrollmentEncounterObs);
		}

		//Set Pat#7 HIV status to positive, date of test {@deliveryDate}
		{
			Patient patient = TestUtils.getPatient(7);
			Obs[] enrollmentEncounterObs = {TestUtils.saveObs(patient, hivStatusConcept, Dictionary.getConcept(Dictionary.POSITIVE), enrollmentDate)};
			TestUtils.saveEncounter(patient, enrollmentEncounterType, enrollmentForm, enrollmentDate, enrollmentEncounterObs);
			TestUtils.saveObs(patient, hivTestDateConcept, deliveryDate, enrollmentDate);
			Obs[] deliveryEncounterObs = {TestUtils.saveObs(patient, deliveryDateConcept, deliveryDate, deliveryDate)};
			TestUtils.saveEncounter(patient, consultationEncounterType, deliveryForm, deliveryDate, deliveryEncounterObs);
		}

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(2, 6, 7, 999);

		CalculationResultMap resultMap = new MchmsHivTestDateCalculation().evaluate(ptIds, null,
				Context.getService(PatientCalculationService.class).createCalculationContext());

		Assert.assertNull(resultMap.get(2)); // Not enrolled into MCHMS
		Assert.assertNull(resultMap.get(6));  //Test date not available
		Assert.assertEquals(deliveryDate, resultMap.get(7).getValue()); //Tested on delivery date
		Assert.assertNull(resultMap.get(999)); // Void patient
	}
}