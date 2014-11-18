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

package org.openmrs.module.kenyaemr.calculation.library.hiv;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link LastCd4PercentageCalculation}
 */
public class LastCd4PercentageCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	/**
	 * @see LastCd4PercentageCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate last CD4 percentage for all patients
	 */
	@Test
	public void evaluate_shouldCalculateLastCD4() throws Exception {
		Concept cd4Percent = Dictionary.getConcept(Dictionary.CD4_PERCENT);

		// Give patient #7 some CD4 obs
		TestUtils.saveObs(TestUtils.getPatient(7), cd4Percent, 20d, TestUtils.date(2012, 12, 1));
		TestUtils.saveObs(TestUtils.getPatient(7), cd4Percent, 30d, TestUtils.date(2010, 11, 1));
		
		List<Integer> ptIds = Arrays.asList(6, 7);
		CalculationResultMap resultMap = new LastCd4PercentageCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertNull(resultMap.get(6)); // has no recorded CD4 percent
		Assert.assertEquals(new Double(20d), ((Obs) resultMap.get(7).getValue()).getValueNumeric());
	}
}