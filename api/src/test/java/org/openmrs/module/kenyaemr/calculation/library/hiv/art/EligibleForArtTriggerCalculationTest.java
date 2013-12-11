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
 * Tests for {@link EligibleForArtTriggerCalculation}
 */
public class EligibleForArtTriggerCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	/**
	 * @see EligibleForArtTriggerCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate the obs which triggered a patient to become eligible for ART
	 * @verifies return null for patients who have never been eligible for ART
	 */
	@Test
	public void evaluate_shouldCalculateEligibilityTrigger() throws Exception {
		Concept diagnosisDate = Dictionary.getConcept(Dictionary.DATE_OF_HIV_DIAGNOSIS);
		Concept cd4 = Dictionary.getConcept(Dictionary.CD4_COUNT);
		Concept whoStage = Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE);

		// Confirm patient #6 HIV+ when they're 1 year old and give them very low CD4 soon after
		TestUtils.saveObs(TestUtils.getPatient(6), diagnosisDate, TestUtils.date(2008, 05, 27), TestUtils.date(2010, 1, 1));
		TestUtils.saveObs(TestUtils.getPatient(6), cd4, 300.0, TestUtils.date(2009, 1, 1));

		// Give patient #7 low CD4 when they're 3 years old and very low CD4 after
		TestUtils.saveObs(TestUtils.getPatient(7), cd4, 900.0, TestUtils.date(1979, 8, 25));
		TestUtils.saveObs(TestUtils.getPatient(7), cd4, 300.0, TestUtils.date(2009, 1, 1));

		// Give patient #8 WHO stage of 3
		TestUtils.saveObs(TestUtils.getPatient(8), whoStage, Dictionary.getConcept(Dictionary.WHO_STAGE_3_PEDS), TestUtils.date(2009, 1, 1));
		
		List<Integer> cohort = Arrays.asList(6, 7, 8, 999);

		CalculationResultMap resultMap = new EligibleForArtTriggerCalculation().evaluate(cohort, null, Context.getService(PatientCalculationService.class).createCalculationContext());

		Obs patient6Trigger = (Obs) resultMap.get(6).getValue(); // Eligible through HIV confirmation
		Assert.assertEquals(diagnosisDate, patient6Trigger.getConcept());
		Assert.assertEquals(TestUtils.date(2008, 05, 27), patient6Trigger.getValueDate());

		Obs patient7Trigger = (Obs) resultMap.get(7).getValue(); // Eligible through CD4 count
		Assert.assertEquals(cd4, patient7Trigger.getConcept());
		Assert.assertEquals(TestUtils.date(1979, 8, 25), patient7Trigger.getObsDatetime());

		Obs patient8Trigger = (Obs) resultMap.get(8).getValue(); // Eligible through WHO stage
		Assert.assertEquals(whoStage, patient8Trigger.getConcept());
		Assert.assertEquals(TestUtils.date(2009, 1, 1), patient8Trigger.getObsDatetime());

		Assert.assertNull(resultMap.get(999)); // Was never eligible for ART
	}
}