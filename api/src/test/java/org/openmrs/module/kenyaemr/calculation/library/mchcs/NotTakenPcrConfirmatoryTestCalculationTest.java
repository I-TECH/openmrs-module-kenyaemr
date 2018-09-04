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
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
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
 * Tests for {@link NotTakenPcrConfirmatoryTestCalculation}
 */
public class NotTakenPcrConfirmatoryTestCalculationTest extends BaseModuleContextSensitiveTest{

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
	 * @see NotTakenPcrConfirmatoryTestCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate infants exited from HEI care and have no pcr confirmatory test
	 */
	@Test
	public void evaluate_shouldCalculateNotTakenPcrConfirmatoryTest() throws Exception {
		//get mchcs program
		Program mchcsProgram = MetadataUtils.existing(Program.class, MchMetadata._Program.MCHCS);

		TestUtils.getPatient(6).setBirthdate(TestUtils.date(2014, 6, 1));

		// Enroll patients #6, #7 and #8 in the mchcs Program
		TestUtils.enrollInProgram(TestUtils.getPatient(6), mchcsProgram, TestUtils.date(2015, 2, 1));
		TestUtils.enrollInProgram(TestUtils.getPatient(7), mchcsProgram, TestUtils.date(2015, 3, 1));
		TestUtils.enrollInProgram(TestUtils.getPatient(8), mchcsProgram, TestUtils.date(2015, 1, 1));


		//get the HIV status of the infant and the if wheather antibody test was done or NOT
		Concept infantHivStatus = Dictionary.getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS);
		Concept pcrTest = Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION_QUALITATIVE);
		Concept pcrContextStatus = Dictionary.getConcept(Dictionary.TEXT_CONTEXT_STATUS);

		//get the hiv status after the exit
		Concept hivStatus = Dictionary.getConcept(Dictionary.HIV_STATUS);
		//#6  HEI and has a pcr positive initial test done
		//set the birthdate of #6 to be this year

		TestUtils.saveObs(TestUtils.getPatient(6),infantHivStatus,Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV),TestUtils.date(2015, 2 ,1));
		TestUtils.saveObs(TestUtils.getPatient(6),pcrContextStatus,Dictionary.getConcept(Dictionary.TEST_STATUS_INITIAL),TestUtils.date(2015, 3 ,1));
		TestUtils.saveObs(TestUtils.getPatient(6),pcrTest,Dictionary.getConcept(Dictionary.POSITIVE),TestUtils.date(2015, 4 ,1));



		//#7  HEI and has completed the Outcomes encounter and has a pcr confirmatory test done
		TestUtils.saveObs(TestUtils.getPatient(7),infantHivStatus,Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV),TestUtils.date(2015, 5 ,1));
		TestUtils.saveObs(TestUtils.getPatient(7),pcrTest,Dictionary.getConcept(Dictionary.POSITIVE),TestUtils.date(2015, 5 ,1));
		TestUtils.saveObs(TestUtils.getPatient(7),pcrContextStatus,Dictionary.getConcept(Dictionary.CONFIRMATION_STATUS),TestUtils.date(2015, 5 ,1));
		Obs[] encounterObss7 = {TestUtils.saveObs(TestUtils.getPatient(7), hivStatus, Dictionary.getConcept(Dictionary.POSITIVE),TestUtils.date(2015, 5 ,1))};

		//make #6 HEI and has completed the Outcomes encounter
		TestUtils.saveObs(TestUtils.getPatient(8),infantHivStatus,Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV),TestUtils.date(2015, 5 ,1));

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(6, 7, 8);
		PatientCalculationService patientCalculationService = Context.getService(PatientCalculationService.class);
		PatientCalculationContext context = patientCalculationService.createCalculationContext();
		context.setNow(TestUtils.date(2015, 9 ,1));

		CalculationResultMap resultMap = new NotTakenPcrConfirmatoryTestCalculation().evaluate(ptIds, null, context);
		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); // HEI with positive initial pcr but has no confirmatory test
		Assert.assertFalse((Boolean) resultMap.get(7).getValue()); //HEI and positive and has the pcr confirmatory test done
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // HEI has no pcr
	}
}
