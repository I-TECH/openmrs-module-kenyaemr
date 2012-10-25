/*
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
package org.openmrs.module.kenyaemr.calculation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DecliningCD4CalculationTest extends BaseModuleContextSensitiveTest {

	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("org/openmrs/module/kenyaemr/include/testData.xml");
	}

	/**
	 * @see DecliningCD4Calculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies determine whether patients have a decline in CD4
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsHasDeclinedCD4() throws Exception {

		// Get HIV Program
		ProgramWorkflowService pws = Context.getProgramWorkflowService();
		Program hivProgram = pws.getPrograms("HIV Program").get(0);

		// Enroll patients #6, #7 and #8 in the HIV Program
		PatientService ps = Context.getPatientService();
		for (int i = 6; i <= 8; ++i) {
			TestUtils.enrollInProgram(ps.getPatient(i), hivProgram, new Date());
		}

		// Give patients #7 and #8 a CD4 count 180 days ago
		Concept cd4 = Context.getConceptService().getConcept(5497);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -180);
		TestUtils.saveObs(Context.getPatientService().getPatient(7), cd4, 123d, calendar.getTime());
		TestUtils.saveObs(Context.getPatientService().getPatient(8), cd4, 123d, calendar.getTime());

		// Give patient #7 a lower CD4 count today
		TestUtils.saveObs(Context.getPatientService().getPatient(7), cd4, 120d, new Date());

		// Give patient #8 a higher CD4 count today
		TestUtils.saveObs(Context.getPatientService().getPatient(8), cd4, 126d, new Date());

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(6, 7, 8, 999);

		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new DecliningCD4Calculation());
		Assert.assertFalse((Boolean) resultMap.get(6).getValue());  //in Hiv program but without cd4 i.e needs cd4
		Assert.assertTrue((Boolean) resultMap.get(7).getValue()); // has decline in CD4
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // has increase in CD4
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // not in HIV Program
	}
}
