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

package org.openmrs.module.kenyaemr.calculation.library.mchcs;

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
 * Tests for {@link InfantNeverTakenProphylaxisCalculation}
 */
public class InfantNeverTakenProphylaxisCalculationTest extends BaseModuleContextSensitiveTest {

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
	 * @see InfantNeverTakenProphylaxisCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate if infant has never been on prophylaxis
	 */
	@Test
	public void evaluate() throws Exception {
		//get mchcs program
		Program mchcsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHCS);

		// Enroll patients #6, #7 and 8  in the mchcs Program
		TestUtils.enrollInProgram(TestUtils.getPatient(6), mchcsProgram, new Date());
		TestUtils.enrollInProgram(TestUtils.getPatient(7), mchcsProgram, new Date());
		TestUtils.enrollInProgram(TestUtils.getPatient(8), mchcsProgram, new Date());

		///get the HIV status of the infant and the if wheather they have been on any prophylaxis
		Concept infantHivStatus = Dictionary.getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS);
		Concept nvp = Dictionary.getConcept(Dictionary.NEVIRAPINE);
		Concept nvpazt3tc = Dictionary.getConcept(Dictionary.LAMIVUDINE_NEVIRAPINE_ZIDOVUDINE);
		Concept medOrders = Dictionary.getConcept(Dictionary.MEDICATION_ORDERS);

		//make patient #6 HEI and  taken any nvp
		TestUtils.saveObs(TestUtils.getPatient(6),infantHivStatus,Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV), new Date());
		TestUtils.saveObs(TestUtils.getPatient(6), medOrders, nvp, TestUtils.date(2013, 1, 1));

		//make patient #7 HEI and  taken any nvpazt3tc
		TestUtils.saveObs(TestUtils.getPatient(7),infantHivStatus,Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV), new Date());
		TestUtils.saveObs(TestUtils.getPatient(7), medOrders, nvpazt3tc, TestUtils.date(2013, 1, 1));

		//make patient #7 HEI and  taken any nothing
		TestUtils.saveObs(TestUtils.getPatient(8),infantHivStatus,Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV), new Date());

		Context.flushSession();

		List<Integer> cohort = Arrays.asList(6, 7, 8);
		CalculationResultMap resultMap = new InfantNeverTakenProphylaxisCalculation().evaluate(cohort, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertFalse((Boolean) resultMap.get(6).getValue()); // is taking NVP
		Assert.assertFalse((Boolean) resultMap.get(7).getValue()); // is not NVP+AZT+3TC
		Assert.assertTrue((Boolean) resultMap.get(8).getValue()); // in HEI but not on any prophylaxis

	}
}
