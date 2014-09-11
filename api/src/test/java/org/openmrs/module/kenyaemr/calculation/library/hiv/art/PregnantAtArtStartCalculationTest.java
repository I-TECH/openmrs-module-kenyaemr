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
 * Tests for {@link PregnantAtArtStartCalculation}
 */
public class PregnantAtArtStartCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	/**
	 * @see PregnantAtArtStartCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate recorded pregnancy status at ART start for all patients
	 */
	@Test
	public void evaluate_shouldCalculatePregnancyStatusAtArtStart() throws Exception {
		Concept pregnancyStatus = Dictionary.getConcept(Dictionary.PREGNANCY_STATUS);
		Concept yes = Dictionary.getConcept(Dictionary.YES);
		Concept no = Dictionary.getConcept(Dictionary.NO);
		Concept stavudine = Dictionary.getConcept(Dictionary.STAVUDINE);

		// For the purposes of this test, everyone is a woman
		TestUtils.getPatient(2).setGender("F");
		TestUtils.getPatient(6).setGender("F");

		// Give patient #2 a YES status on same day as ART start. This patient has a drug order in standardTestDataset.xml
		// which is determining their ART start date
		TestUtils.saveObs(TestUtils.getPatient(2), pregnancyStatus, yes, TestUtils.date(2007, 12, 25));

		// Give patient #6 a YES status week before ART start
		TestUtils.saveObs(TestUtils.getPatient(6), pregnancyStatus, yes, TestUtils.date(2012, 1, 1));
		TestUtils.saveDrugOrder(TestUtils.getPatient(6), stavudine, TestUtils.date(2012, 1, 8), null);

		// Give patient #7 a YES but a newer NO status before ART start
		TestUtils.saveObs(TestUtils.getPatient(7), pregnancyStatus, yes, TestUtils.date(2012, 1, 1));
		TestUtils.saveObs(TestUtils.getPatient(7), pregnancyStatus, no, TestUtils.date(2012, 1, 3));
		TestUtils.saveDrugOrder(TestUtils.getPatient(7), stavudine, TestUtils.date(2012, 1, 8), null);

		// Give patient #8 a YES status week before ART start and a newer NO status after ART start
		TestUtils.saveObs(TestUtils.getPatient(8), pregnancyStatus, yes, TestUtils.date(2012, 1, 1));
		TestUtils.saveObs(TestUtils.getPatient(8), pregnancyStatus, no, TestUtils.date(2012, 1, 15));
		TestUtils.saveDrugOrder(TestUtils.getPatient(8), stavudine, TestUtils.date(2012, 1, 8), null);
		
		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8, 999);
		CalculationResultMap resultMap = new PregnantAtArtStartCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertTrue((Boolean) resultMap.get(2).getValue());
		Assert.assertTrue((Boolean) resultMap.get(6).getValue());
		Assert.assertFalse((Boolean) resultMap.get(7).getValue());
		Assert.assertTrue((Boolean) resultMap.get(8).getValue());
		Assert.assertFalse((Boolean) resultMap.get(999).getValue()); // has no recorded status
	}
}