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
import org.openmrs.*;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LostToFollowUpCalculationTest extends BaseModuleContextSensitiveTest {

	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("org/openmrs/module/kenyaemr/include/testData.xml");
	}

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.MissedAppointmentsOrDefaultedCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies determine whether patients are lost to follow up
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsAreLostToFollowUp() throws Exception {

		// Get HIV Program
		Program hivProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.HIV_PROGRAM_UUID);

		// Enroll patients #6, #7, #8 in the HIV Program
		PatientService ps = Context.getPatientService();
		for (int i = 6; i <= 8; ++i) {
			TestUtils.enrollInProgram(ps.getPatient(i), hivProgram, TestUtils.date(2011, 1, 1));
		}

		// Give patient #7 a scheduled encounter 200 days ago
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -200);
		EncounterType scheduledEncType = Context.getEncounterService().getEncounterType("Scheduled");
		TestUtils.saveEncounter(ps.getPatient(7), scheduledEncType, calendar.getTime());

		// Give patient #8 a scheduled encounter 10 days ago
		calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -10);
		TestUtils.saveEncounter(ps.getPatient(8), scheduledEncType, calendar.getTime());

		Context.flushSession();
		Context.clearSession();

		List<Integer> ptIds = Arrays.asList(6, 7, 8, 999);

		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new LostToFollowUpCalculation());
		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); // patient in HIV program and no encounters
		Assert.assertTrue((Boolean) resultMap.get(7).getValue()); // patient in HIV program and no encounter in last X days
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // patient in HIV program and has encounter in last X days
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // patient not in HIV Program
	}
}
