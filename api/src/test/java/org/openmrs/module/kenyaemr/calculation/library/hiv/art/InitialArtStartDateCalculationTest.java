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

package org.openmrs.module.kenyaemr.calculation.library.hiv.art;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link InitialArtStartDateCalculation}
 */
public class InitialArtStartDateCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	/**
	 * @see InitialArtStartDateCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateInitialArtStartDate() throws Exception {
		Concept dapsone = Dictionary.getConcept(Dictionary.DAPSONE);
		Concept azt = Context.getConceptService().getConcept(86663);
		Concept _3tc = Context.getConceptService().getConcept(78643);
		Concept efv = Context.getConceptService().getConcept(75523);

		// Put patient #6 on Dapsone
		TestUtils.saveDrugOrder(TestUtils.getPatient(6), dapsone, TestUtils.date(2011, 1, 1), null);

		// Put patient #7 on AZT, then 3TC, then EFV
		TestUtils.saveDrugOrder(TestUtils.getPatient(7), azt, TestUtils.date(2010, 1, 1), TestUtils.date(2011, 1, 1));
		TestUtils.saveDrugOrder(TestUtils.getPatient(7), _3tc, TestUtils.date(2011, 1, 1), TestUtils.date(2012, 1, 1));
		TestUtils.saveDrugOrder(TestUtils.getPatient(7), efv, TestUtils.date(2011, 1, 1), TestUtils.date(2012, 1, 1));
		
		List<Integer> cohort = Arrays.asList(6, 7, 8);

		CalculationResultMap resultMap = new InitialArtStartDateCalculation().evaluate(cohort, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertNull(resultMap.get(6)); // isn't on any ART drugs
		Assert.assertEquals(TestUtils.date(2010, 1, 1), resultMap.get(7).getValue());
		Assert.assertNull(resultMap.get(8)); // isn't on any drugs
	}
}