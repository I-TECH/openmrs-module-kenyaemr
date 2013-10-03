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
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link CurrentArtRegimenCalculation}
 */
public class CurrentArtRegimenCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	/**
	 * @see CurrentArtRegimenCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateCurrentArtRegimen() throws Exception {

		// Put patient #7 on Dapsone
		Concept dapsone = Dictionary.getConcept(Dictionary.DAPSONE);
		TestUtils.saveDrugOrder(Context.getPatientService().getPatient(7), dapsone, TestUtils.date(2011, 1, 1), null);

		// Put patient #8 on Stavudine
		Concept stavudine = Context.getConceptService().getConcept(84309);
		TestUtils.saveDrugOrder(Context.getPatientService().getPatient(8), stavudine, TestUtils.date(2011, 1, 1), null);

		Context.flushSession();
		
		List<Integer> cohort = Arrays.asList(6, 7, 8);

		CalculationResultMap resultMap = new CurrentArtRegimenCalculation().evaluate(cohort, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertNull(resultMap.get(6)); // isn't on any drugs
		Assert.assertNull(resultMap.get(7)); // isn't on any ARTs

		RegimenOrder pat8Res = (RegimenOrder)resultMap.get(8).getValue();
		Assert.assertEquals(1, pat8Res.getDrugOrders().size());
	}
}