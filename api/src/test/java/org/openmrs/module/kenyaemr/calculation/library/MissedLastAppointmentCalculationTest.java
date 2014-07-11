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

package org.openmrs.module.kenyaemr.calculation.library;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests for {@link MissedLastAppointmentCalculation}
 */
public class MissedLastAppointmentCalculationTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		hivMetadata.install();
	}

	/**
	 * @see MissedLastAppointmentCalculation#getFlagMessage()
	 */
	@Test
	public void getFlagMessage() {
		Assert.assertThat(new MissedLastAppointmentCalculation().getFlagMessage(), notNullValue());
	}

	/**
	 * @see MissedLastAppointmentCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsWhoMissedAppointmentsOrDefaulted() throws Exception {
		// Give patient #7 a return visit obs of 10 days ago
		Concept returnVisit = Context.getConceptService().getConcept(5096);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -10);
		TestUtils.saveObs(TestUtils.getPatient(7), returnVisit, calendar.getTime(), calendar.getTime());

		// Give patient #8 a return visit obs of 10 days in the future
		calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 10);
		TestUtils.saveObs(TestUtils.getPatient(8), returnVisit, calendar.getTime(), calendar.getTime());

		List<Integer> cohort = Arrays.asList(6, 7, 8, 9);

		CalculationResultMap resultMap = new MissedLastAppointmentCalculation().evaluate(cohort, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertFalse((Boolean) resultMap.get(6).getValue()); // patient has no return visit obs
		Assert.assertTrue((Boolean) resultMap.get(7).getValue()); // patient has missed visit
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // patient has future return visit date
	}
}
