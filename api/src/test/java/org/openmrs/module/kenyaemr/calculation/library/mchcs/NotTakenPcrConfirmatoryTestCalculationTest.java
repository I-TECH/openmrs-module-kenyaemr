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
		// Enroll patients #6 and  #7  in the mchcs Program
		TestUtils.enrollInProgram(TestUtils.getPatient(6), mchcsProgram, new Date());
		TestUtils.enrollInProgram(TestUtils.getPatient(7), mchcsProgram, new Date());
		TestUtils.enrollInProgram(TestUtils.getPatient(8), mchcsProgram, new Date());

		//getting an encounter required before confirmation is done
		EncounterType heiOutcomesEncounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHCS_HEI_COMPLETION);
		Form heiCompletionForm = MetadataUtils.existing(Form.class, MchMetadata._Form.MCHCS_HEI_COMPLETION);

		//get the HIV status of the infant and the if wheather antibody test was done or NOT
		Concept infantHivStatus = Dictionary.getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS);
		Concept pcrTest = Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION);
		Concept status = Dictionary.getConcept(Dictionary.CONFIRMATION_STATUS);
		//get the hiv status after the exit
		Concept hivStatus = Dictionary.getConcept(Dictionary.HIV_STATUS);

		//make #6 HEI and has completed the Outcomes encounter
		TestUtils.saveObs(TestUtils.getPatient(6),infantHivStatus,Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV),new Date());
		//#6 has pcr test done with initial status
		//TestUtils.saveObs(ps.getPatient(6),pcrTest,Dictionary.getConcept(Dictionary.TEST_STATUS_INITIAL),new Date());
		//collect some observations from the hei outcomes form  for patient #6
		Obs[] encounterObss6 = {TestUtils.saveObs(TestUtils.getPatient(6), hivStatus, Dictionary.getConcept(Dictionary.POSITIVE), new Date())};
		// save the entire encounter for patient #6
		TestUtils.saveEncounter(TestUtils.getPatient(6), heiOutcomesEncounterType,heiCompletionForm, new Date(), encounterObss6);

		//#7  HEI and has completed the Outcomes encounter and has a pcr confirmatory test done
		TestUtils.saveObs(TestUtils.getPatient(7),infantHivStatus,Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV),new Date());
		TestUtils.saveObs(TestUtils.getPatient(7),pcrTest,Dictionary.getConcept(Dictionary.CONFIRMATION_STATUS),new Date());
		Obs[] encounterObss7 = {TestUtils.saveObs(TestUtils.getPatient(7), hivStatus, Dictionary.getConcept(Dictionary.POSITIVE), new Date())};
		TestUtils.saveEncounter(TestUtils.getPatient(7), heiOutcomesEncounterType,heiCompletionForm, new Date(), encounterObss7);

		//#8  HEI and has NOT completed the Outcomes encounter and has a pcr initial done test done
		TestUtils.saveObs(TestUtils.getPatient(8),infantHivStatus,Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV),new Date());
		TestUtils.saveObs(TestUtils.getPatient(8),pcrTest,Dictionary.getConcept(Dictionary.TEST_STATUS_INITIAL),new Date());

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(6, 7, 8);
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new NotTakenPcrConfirmatoryTestCalculation());
		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); // HEI has no pcr and hei outcome encounter completed
		Assert.assertTrue((Boolean) resultMap.get(7).getValue()); //has the pcr confirmatory test done
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // has no hei outcomes encounter
	}
}
