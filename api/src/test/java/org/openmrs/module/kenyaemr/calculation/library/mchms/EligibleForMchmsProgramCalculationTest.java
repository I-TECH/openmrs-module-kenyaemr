/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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