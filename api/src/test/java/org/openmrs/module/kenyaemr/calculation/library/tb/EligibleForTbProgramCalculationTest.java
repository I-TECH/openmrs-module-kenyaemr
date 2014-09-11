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
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link EligibleForTbProgramCalculation}
 */
public class EligibleForTbProgramCalculationTest extends BaseModuleContextSensitiveTest {

	@Test
	public void evaluate_shouldReturnTrueForAllAlivePatients() {

		// Mark patient #8 as deceased on 1st Jan 2012
		TestUtils.getPatient(8).setDead(true);
		TestUtils.getPatient(8).setDeathDate(TestUtils.date(2012, 1, 1));

		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8);
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new EligibleForTbProgramCalculation());
		Assert.assertTrue((Boolean) resultMap.get(2).getValue());
		Assert.assertTrue((Boolean) resultMap.get(6).getValue());
		Assert.assertTrue((Boolean) resultMap.get(7).getValue());
		Assert.assertFalse((Boolean) resultMap.get(8).getValue()); // Deceased
	}
}