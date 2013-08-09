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

package org.openmrs.module.kenyaemr.calculation.library.tb;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyautil.MetadataUtils;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

public class NeverScreenedForTbCalculationTest extends BaseModuleContextSensitiveTest {

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
	}

	@Test
	public void evaluate_shouldReturnHivPatientsNotScreenedForTb() throws Exception {

		// Get HIV Program and TB screening encounter type
		Program hivProgram = MetadataUtils.getProgram(Metadata.HIV_PROGRAM);
		EncounterType screeningEncType = MetadataUtils.getEncounterType(Metadata.TB_SCREENING_ENCOUNTER_TYPE);

		// Enroll patients #6 and #7
		PatientService ps = Context.getPatientService();
		TestUtils.enrollInProgram(ps.getPatient(6), hivProgram, TestUtils.date(2011, 1, 1));
		TestUtils.enrollInProgram(ps.getPatient(7), hivProgram, TestUtils.date(2011, 1, 1));

		// Screen patient #6 for TB a year later
		TestUtils.saveEncounter(ps.getPatient(6), screeningEncType, TestUtils.date(2012, 1, 1));

		Context.flushSession();
		
		List<Integer> cohort = Arrays.asList(6, 7, 8);
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(cohort, new NeverScreenedForTbCalculation());
		Assert.assertFalse((Boolean) resultMap.get(6).getValue());
		Assert.assertTrue((Boolean) resultMap.get(7).getValue());
		Assert.assertNull(resultMap.get(8)); // not in HIV program
	}
}