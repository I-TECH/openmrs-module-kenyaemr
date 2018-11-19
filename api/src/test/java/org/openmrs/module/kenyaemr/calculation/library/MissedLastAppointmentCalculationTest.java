/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
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
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		TestUtils.enrollInProgram(TestUtils.getPatient(7), hivProgram, TestUtils.date(2000, 6, 1));
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
