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

package org.openmrs.module.kenyaemr.calculation.library;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests for {@link IsPregnantCalculation}
 */
public class IsPregnantCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	/**
	 * @see IsPregnantCalculation#getFlagMessage()
	 */
	@Test
	public void getFlagMessage() {
		Assert.assertThat(new IsPregnantCalculation().getFlagMessage(), notNullValue());
	}

	/**
	 * @see IsPregnantCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate last recorded pregnancy status for all patients
	 */
	@Test
	public void evaluate_shouldCalculatePregnancyStatus() throws Exception {
		Concept pregnancyStatus = Dictionary.getConcept(Dictionary.PREGNANCY_STATUS);
		Concept yes = Dictionary.getConcept(Dictionary.YES);
		Concept no = Dictionary.getConcept(Dictionary.NO);

		// Give patient #6 a recent YES recording which should be ignored as they are male
		TestUtils.saveObs(TestUtils.getPatient(6), pregnancyStatus, yes, TestUtils.date(2012, 12, 1));

		// Give patient #7 an older YES recording
		TestUtils.saveObs(TestUtils.getPatient(7), pregnancyStatus, yes, TestUtils.date(2010, 11, 1));

		// Give patient #7 a recent NO recording
		TestUtils.saveObs(TestUtils.getPatient(7), pregnancyStatus, no, TestUtils.date(2012, 12, 1));

		// Give patient #8 a recent YES recording
		TestUtils.saveObs(TestUtils.getPatient(8), pregnancyStatus, yes, TestUtils.date(2012, 12, 1));
		
		List<Integer> ptIds = Arrays.asList(6, 7, 8);
		CalculationResultMap resultMap = new IsPregnantCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertFalse((Boolean) resultMap.get(6).getValue()); // is male
		Assert.assertFalse((Boolean) resultMap.get(7).getValue());
		Assert.assertTrue((Boolean) resultMap.get(8).getValue());
	}
}