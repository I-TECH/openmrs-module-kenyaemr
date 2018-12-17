/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link LastWhoStageCalculation}
 */
public class LastWhoStageCalculationTest extends BaseModuleContextSensitiveTest {

	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	/**
	 * @see LastWhoStageCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate last recorded WHO stage for all patients
	 */
	@Test
	public void evaluate_shouldCalculateLatestWHOStage() throws Exception {
		Concept whoStage = Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE);

		// Give patient #7 a recent WHO ADULT 1 STAGE recording
		Concept whoAdult1 = Dictionary.getConcept(Dictionary.WHO_STAGE_1_ADULT);
		TestUtils.saveObs(TestUtils.getPatient(7), whoStage, whoAdult1, TestUtils.date(2012, 12, 1));

		// Give patient #7 an older WHO ADULT 2 STAGE recording
		Concept whoAdult2 = Dictionary.getConcept(Dictionary.WHO_STAGE_2_ADULT);
		TestUtils.saveObs(TestUtils.getPatient(7), whoStage, whoAdult2, TestUtils.date(2010, 11, 1));
		
		List<Integer> ptIds = Arrays.asList(6, 7);
		CalculationResultMap resultMap = new LastWhoStageCalculation().evaluate(ptIds, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertNull(resultMap.get(6)); // has no recorded stage
		Assert.assertEquals(whoAdult1, ((Obs) resultMap.get(7).getValue()).getValueCoded());
	}
}