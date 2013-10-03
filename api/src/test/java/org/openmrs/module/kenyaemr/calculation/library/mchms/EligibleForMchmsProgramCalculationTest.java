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

package org.openmrs.module.kenyaemr.calculation.library.mchms;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link EligibleForMchmsProgramCalculation}
 */
public class EligibleForMchmsProgramCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.library.mchms.EligibleForMchmsProgramCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldDetermineWhetherPatientsAreNotOnArt() throws Exception {
		List<Integer> ptIds = Arrays.asList(6, 7);

		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new EligibleForMchmsProgramCalculation());

		Assert.assertThat((Boolean) resultMap.get(6).getValue(), is(false)); // is male
		Assert.assertThat((Boolean) resultMap.get(7).getValue(), is(true)); // is female
	}
}