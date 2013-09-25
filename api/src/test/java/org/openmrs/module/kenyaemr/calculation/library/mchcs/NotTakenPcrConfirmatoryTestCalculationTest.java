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
import org.openmrs.*;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.calculation.library.mchcs.NotTakenPcrConfirmatoryTestCalculation }
 */
public class NotTakenPcrConfirmatoryTestCalculationTest extends BaseModuleContextSensitiveTest{

	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-metadata.xml");
	}
	/**
	 * @see org.openmrs.module.kenyaemr.calculation.library.NotTakenPcrConfirmatoryTestCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate infants exited from HEI care and have no pcr confirmatory test
	 */
	@Test
	public void evaluate_shouldCalculateNotTakenPcrConfirmatoryTest() throws Exception {
		//get mchcs program
		Program mchcsProgram = MetadataUtils.getProgram(MchMetadata.Program.MCHCS);
		// Enroll patients #6 and  #7  in the mchcs Program
		PatientService ps = Context.getPatientService();
		TestUtils.enrollInProgram(ps.getPatient(6), mchcsProgram, new Date());
		TestUtils.enrollInProgram(ps.getPatient(7), mchcsProgram, new Date());
		TestUtils.enrollInProgram(ps.getPatient(8), mchcsProgram, new Date());

		//getting an encounter required before confirmation is done
		EncounterType heiOutcomesEncounterType = MetadataUtils.getEncounterType(MchMetadata.EncounterType.MCHCS_HEI_COMPLETION);
		Form heiCompletionForm = MetadataUtils.getForm(MchMetadata.Form.MCHCS_HEI_COMPLETION);

		//get the HIV status of the infant and the if wheather antibody test was done or NOT
		Concept infantHivStatus = Dictionary.getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS);
		Concept pcrTest = Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION);
		Concept status = Dictionary.getConcept(Dictionary.CONFIRMATION_STATUS);
		//get the hiv status after the exit
		Concept hivStatus = Dictionary.getConcept(Dictionary.HIV_STATUS);

		//make #6 HEI and has completed the Outcomes encounter
		TestUtils.saveObs(ps.getPatient(6),infantHivStatus,Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV),new Date());
		//#6 has pcr test done with initial status
		//TestUtils.saveObs(ps.getPatient(6),pcrTest,Dictionary.getConcept(Dictionary.TEST_STATUS_INITIAL),new Date());
		//collect some observations from the hei outcomes form  for patient #6
		Obs[] encounterObss6 = {TestUtils.saveObs(ps.getPatient(6), hivStatus, Dictionary.getConcept(Dictionary.POSITIVE), new Date())};
		// save the entire encounter for patient #6
		TestUtils.saveEncounter(ps.getPatient(6), heiOutcomesEncounterType,heiCompletionForm, new Date(), encounterObss6);

		//#7  HEI and has completed the Outcomes encounter and has a pcr confirmatory test done
		TestUtils.saveObs(ps.getPatient(7),infantHivStatus,Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV),new Date());
		TestUtils.saveObs(ps.getPatient(7),pcrTest,Dictionary.getConcept(Dictionary.CONFIRMATION_STATUS),new Date());
		Obs[] encounterObss7 = {TestUtils.saveObs(ps.getPatient(7), hivStatus, Dictionary.getConcept(Dictionary.POSITIVE), new Date())};
		TestUtils.saveEncounter(ps.getPatient(7), heiOutcomesEncounterType,heiCompletionForm, new Date(), encounterObss7);

		//#8  HEI and has NOT completed the Outcomes encounter and has a pcr initial done test done
		TestUtils.saveObs(ps.getPatient(8),infantHivStatus,Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV),new Date());
		TestUtils.saveObs(ps.getPatient(8),pcrTest,Dictionary.getConcept(Dictionary.TEST_STATUS_INITIAL),new Date());

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(6, 7, 8);
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new NotTakenPcrConfirmatoryTestCalculation());
		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); // HEI has no pcr and hei outcome encounter completed
		Assert.assertFalse((Boolean) resultMap.get(7).getValue()); //has the pcr confirmatory test done
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // has no hei outcomes encounter

	}


}
