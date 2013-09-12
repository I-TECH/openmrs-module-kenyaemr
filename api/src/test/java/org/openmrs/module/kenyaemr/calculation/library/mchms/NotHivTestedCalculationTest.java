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
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class NotHivTestedCalculationTest extends BaseModuleContextSensitiveTest {

	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("test-data.xml");
	}

	/**
	 * @verifies determine whether MCH-MS patients have been tested for HIV
	 * @see org.openmrs.module.kenyaemr.calculation.library.mchms.NotHivTestedCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsHaveBeenHivTested() throws Exception {

		// Get the MCH-MS Program
		Program mchmsProgram = MetadataUtils.getProgram(Metadata.Program.MCHMS);

		//Enroll patients #1, #2 and #3 into MCH-MS
		PatientService patientService = Context.getPatientService();
		for (int i = 1; i <= 3; i++) {
			TestUtils.enrollInProgram(patientService.getPatient(i), mchmsProgram, new Date());
		}

		//Get the HIV Status concept
		Concept hivStatus = Dictionary.getConcept(Dictionary.HIV_STATUS);

		//Indicate HIV Status for patient 1 as Not Tested for one patient
		TestUtils.saveObs(patientService.getPatient(1), hivStatus, Dictionary.getConcept(Dictionary.NOT_HIV_TESTED), new Date());

		//Indicate HIV Status for patient 2 as Negative for one patient
		TestUtils.saveObs(patientService.getPatient(1), hivStatus, Dictionary.getConcept(Dictionary.NEGATIVE), new Date());

		//Indicate HIV Status for patient 3 as Positive for one patient
		TestUtils.saveObs(patientService.getPatient(1), hivStatus, Dictionary.getConcept(Dictionary.POSITIVE), new Date());

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(1, 2, 3, 999);

		//Run NotHivTestedCalculation with these test patients
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new NotHivTestedCalculation());

		Assert.assertTrue((Boolean) resultMap.get(1).getValue());  //in MCH-MS program but no HIV Status - Need to be tested
		Assert.assertFalse((Boolean) resultMap.get(2).getValue()); //in MCH-MS program but has -ve HIV Status
		Assert.assertFalse((Boolean) resultMap.get(3).getValue()); //in MCH-MS program but has +ve HIV Status
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // Not in MCH-MS Program
	}
}
