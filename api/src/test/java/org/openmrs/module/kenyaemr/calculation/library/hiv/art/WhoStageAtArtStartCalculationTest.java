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
 * Tests for {@link org.openmrs.module.kenyaemr.calculation.library.hiv.art.WhoStageAtArtStartCalculation}
 */
public class WhoStageAtArtStartCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.library.hiv.art.WhoStageAtArtStartCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateWhoStageAtArtStart() throws Exception {
		Concept whoStage = Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE);
		Concept whoStage1Adults = Dictionary.getConcept(Dictionary.WHO_STAGE_1_ADULT);
		Concept whoStage2Adults = Dictionary.getConcept(Dictionary.WHO_STAGE_2_ADULT);
		Concept whoStageUnknown = Dictionary.getConcept(Dictionary.UNKNOWN);

		Concept stavudine = Dictionary.getConcept(Dictionary.STAVUDINE);

		// Give patient #2 WHO Stage 1 on same day as ART start. This patient has a drug order in standardTestDataset.xml
		// which is determining their ART start date
		TestUtils.saveObs(TestUtils.getPatient(2), whoStage, whoStage1Adults, TestUtils.date(2007, 12, 25));

		// Give patient #6 WHO Stage 1 week before ART start
		TestUtils.saveObs(TestUtils.getPatient(6), whoStage, whoStage1Adults, TestUtils.date(2012, 1, 1));
		TestUtils.saveDrugOrder(TestUtils.getPatient(6), stavudine, TestUtils.date(2012, 1, 8), null);

		// Give patient #7 WHO Stage 1 but a newer UNKNOWN WHO stage (saved as NULL) before ART start
		TestUtils.saveObs(TestUtils.getPatient(7), whoStage, whoStage1Adults, TestUtils.date(2012, 1, 1));
		TestUtils.saveObs(TestUtils.getPatient(7), whoStage, whoStageUnknown, TestUtils.date(2012, 1, 3));
		TestUtils.saveDrugOrder(TestUtils.getPatient(7), stavudine, TestUtils.date(2012, 1, 8), null);

		// Give patient #8 WHO Stage 1 week before ART start and a newer WHO Stage 2 after ART start
		TestUtils.saveObs(TestUtils.getPatient(8), whoStage, whoStage1Adults, TestUtils.date(2012, 1, 1));
		TestUtils.saveObs(TestUtils.getPatient(8), whoStage, whoStage2Adults, TestUtils.date(2012, 1, 15));
		TestUtils.saveDrugOrder(TestUtils.getPatient(8), stavudine, TestUtils.date(2012, 1, 8), null);

		Context.flushSession();

		List<Integer> ptIds = Arrays.asList(2, 6, 7, 8, 999);
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new WhoStageAtArtStartCalculation());
		Assert.assertEquals(1, resultMap.get(2).getValue());
		Assert.assertEquals(1, resultMap.get(6).getValue());
		Assert.assertNull(resultMap.get(7)); //has unknown/null latest WHO Stage
		Assert.assertEquals(1, resultMap.get(8).getValue());
		Assert.assertNull(resultMap.get(999)); //has no recorded WHO stage
	}
}