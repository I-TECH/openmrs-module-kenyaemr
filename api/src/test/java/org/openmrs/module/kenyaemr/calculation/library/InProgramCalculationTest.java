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
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests for {@link InProgramCalculation}
 */
public class InProgramCalculationTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private TbMetadata tbMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		hivMetadata.install();
		tbMetadata.install();
	}

	/**
	 * @see InProgramCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate() throws Exception {
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Program tbProgram = MetadataUtils.existing(Program.class, TbMetadata._Program.TB);

		// Enroll patient #6 in TB program
		TestUtils.enrollInProgram(TestUtils.getPatient(6), tbProgram, TestUtils.date(2012, 1, 1));

		// Enroll patient #7 in HIV program
		TestUtils.enrollInProgram(TestUtils.getPatient(7), hivProgram, TestUtils.date(2012, 1, 1));

		// Enroll patient #8 in HIV program but also discontinue them
		TestUtils.enrollInProgram(TestUtils.getPatient(8), tbProgram, TestUtils.date(2012, 1, 1), TestUtils.date(2012, 6, 1));
		
		List<Integer> ptIds = Arrays.asList(6, 7, 8);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("program", hivProgram);
		CalculationResultMap resultMap = new InProgramCalculation().evaluate(ptIds, params, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertFalse((Boolean) resultMap.get(6).getValue()); // Wrong program
		Assert.assertTrue((Boolean) resultMap.get(7).getValue());
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // since discontinued
	}
}