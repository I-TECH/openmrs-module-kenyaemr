/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link InTbProgramCalculation}
 */
public class InTbProgramCalculationTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private TbMetadata tbMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		tbMetadata.install();
	}

	/**
	 * @see InTbProgramCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate() throws Exception {

		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);

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