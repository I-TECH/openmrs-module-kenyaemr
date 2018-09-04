/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.mchms;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
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
 * Tests for {@link NotHivTestedCalculation}
 */
public class NotHivTestedCalculationTest extends BaseModuleContextSensitiveTest {

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
	 * @see NotHivTestedCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies determine whether MCH-MS patients have been tested for HIV
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsHaveNotBeenHivTested() throws Exception {

		// Get the MCH-MS Program
		Program mchmsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHMS);

		//Enroll patients #6, #7 and #8 into MCH-MS
		for (int i = 6; i <= 8; i++) {
			TestUtils.enrollInProgram(TestUtils.getPatient(i), mchmsProgram, new Date());
		}

		//Get the HIV Status concept
		Concept hivStatus = Dictionary.getConcept(Dictionary.HIV_STATUS);

		//Indicate HIV Status for patient 6 as Not Tested
		TestUtils.saveObs(TestUtils.getPatient(6), hivStatus, Dictionary.getConcept(Dictionary.NOT_HIV_TESTED), new Date());

		//Indicate HIV Status for patient 7 as Negative
		TestUtils.saveObs(TestUtils.getPatient(7), hivStatus, Dictionary.getConcept(Dictionary.NEGATIVE), new Date());

		//Indicate HIV Status for patient 8 as Positive
		TestUtils.saveObs(TestUtils.getPatient(8), hivStatus, Dictionary.getConcept(Dictionary.POSITIVE), new Date());

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(6, 7, 8, 2, 999);

		//Run NotHivTestedCalculation with these test patients
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new NotHivTestedCalculation());

		Assert.assertTrue((Boolean) resultMap.get(6).getValue());  //in MCH-MS program but no HIV Status - Need to be tested
		Assert.assertFalse((Boolean) resultMap.get(7).getValue()); //in MCH-MS program but has -ve HIV Status
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); //in MCH-MS program but has +ve HIV Status
		Assert.assertFalse((Boolean) resultMap.get(2).getValue()); // Not in MCH-MS Program
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // Voided patient
	}
}