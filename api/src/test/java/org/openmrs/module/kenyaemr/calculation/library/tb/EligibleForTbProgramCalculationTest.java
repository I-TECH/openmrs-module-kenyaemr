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