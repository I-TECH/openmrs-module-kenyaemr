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

/**
 * Tests for {@link org.openmrs.module.kenyaemr.calculation.library.tb.InTbProgramCalculation}
 */
public class InTbProgramCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
	}

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.library.tb.InTbProgramCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate() throws Exception {

		Program tbProgram = MetadataUtils.getProgram(Metadata.TB_PROGRAM);

		// Enroll patient #6
		PatientService ps = Context.getPatientService();
		TestUtils.enrollInProgram(ps.getPatient(6), tbProgram, TestUtils.date(2011, 1, 1));

		// Enroll patient #7 but complete a year later
		TestUtils.enrollInProgram(ps.getPatient(7), tbProgram, TestUtils.date(2011, 1, 1), TestUtils.date(2012, 1, 1));
		
		List<Integer> cohort = Arrays.asList(6, 7, 8);
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(cohort, new InTbProgramCalculation());
		Assert.assertTrue((Boolean) resultMap.get(6).getValue()); // is still in program
		Assert.assertFalse((Boolean) resultMap.get(7).getValue()); // discontinued
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // was never in program
	}
}