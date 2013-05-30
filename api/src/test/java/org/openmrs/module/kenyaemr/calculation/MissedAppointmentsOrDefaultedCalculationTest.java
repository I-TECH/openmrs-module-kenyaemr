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
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MissedAppointmentsOrDefaultedCalculationTest extends BaseModuleContextSensitiveTest {

	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("test-data.xml");
	}

	/**
	 * @see MissedAppointmentsOrDefaultedCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies determine whether patients have a Missed appointments or defaulted
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsWhoMissedAppointmentsOrDefaulted() throws Exception {

		// Get HIV Program
		Program hivProgram = Metadata.getProgram(Metadata.HIV_PROGRAM);

		// Enroll patients #6, #7, #8 in the HIV Program
		PatientService ps = Context.getPatientService();
		for (int i = 6; i <= 8; ++i) {
			TestUtils.enrollInProgram(ps.getPatient(i), hivProgram, TestUtils.date(2011, 1, 1));
		}

		// Give patient #7 a return visit obs of 10 days ago
		Concept returnVisit = Context.getConceptService().getConcept(5096);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -10);
		TestUtils.saveObs(Context.getPatientService().getPatient(7), returnVisit, calendar.getTime(), calendar.getTime());

		// Give patient #8 a return visit obs of 10 days in the future
		calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 10);
		TestUtils.saveObs(Context.getPatientService().getPatient(8), returnVisit, calendar.getTime(), calendar.getTime());

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(6, 7, 8, 9);

		CalculationResultMap resultMap = new MissedAppointmentsOrDefaultedCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertFalse((Boolean) resultMap.get(6).getValue()); // patient in HIV program but no return visit obs
		Assert.assertTrue((Boolean) resultMap.get(7).getValue()); // patient has missed visit
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // patient has future return visit date
		Assert.assertFalse((Boolean) resultMap.get(9).getValue()); // patient not in HIV Program
	}
}
