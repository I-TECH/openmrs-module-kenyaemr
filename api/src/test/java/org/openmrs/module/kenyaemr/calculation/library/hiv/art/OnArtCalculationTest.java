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
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link OnArtCalculation}
 */

public class OnArtCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	/**
	 * @see OnArtCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateCurrentArtRegimen() throws Exception {
		/**
		 * encounter#3,4,5 for patient 7
		 * encounter#6 for patient 2
		 *
		 */
		// Put patient #7 on Dapsone
		Concept dapsone = Dictionary.getConcept(Dictionary.DAPSONE);
		Encounter enc1 = Context.getEncounterService().getEncounter(3);
		TestUtils.saveDrugOrder(TestUtils.getPatient(7), dapsone, TestUtils.date(2011, 1, 1), null, enc1);

		// Put patient #8 on Stavudine
		Concept stavudine = Dictionary.getConcept(Dictionary.STAVUDINE);
		Encounter enc2 = Context.getEncounterService().getEncounter(6);
		TestUtils.saveDrugOrder(TestUtils.getPatient(2), stavudine, TestUtils.date(2011, 1, 1), null, enc2);

		List<Integer> cohort = Arrays.asList(2, 7, 8);

		CalculationResultMap resultMap = new OnArtCalculation().evaluate(cohort, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertThat((Boolean) resultMap.get(8).getValue(), is(false)); // isn't on any drugs
		Assert.assertThat((Boolean) resultMap.get(7).getValue(), is(false)); // isn't on any ARTs
		Assert.assertThat((Boolean) resultMap.get(2).getValue(), is(true)); // is taking D4T
	}
}