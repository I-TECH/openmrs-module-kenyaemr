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

package org.openmrs.module.kenyaemr.calculation.library.art;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.calculation.library.art.LastWhoStageCalculation}
 */
public class LastWhoStageCalculationTest extends BaseModuleContextSensitiveTest {

	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("test-data.xml");
	}

	/**
	 * @see NeedsCd4TestCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate last recorded WHO stage for all patients
	 */
	@Test
	public void evaluate_shouldCalculateLatestWHOStage() throws Exception {

		PatientService ps = Context.getPatientService();
		Concept whoStage = Dictionary.getConcept(Dictionary.CURRENT_WHO_STAGE);

		// Give patient #6 a recent WHO ADULT 1 STAGE recording
		Concept whoAdult1 = Dictionary.getConcept(Dictionary.WHO_STAGE_1_ADULT);
		TestUtils.saveObs(ps.getPatient(6), whoStage, whoAdult1, TestUtils.date(2012, 12, 1));

		// Give patient #6 an older WHO ADULT 2 STAGE recording
		Concept whoAdult2 = Dictionary.getConcept(Dictionary.WHO_STAGE_2_ADULT);
		TestUtils.saveObs(ps.getPatient(6), whoStage, whoAdult2, TestUtils.date(2010, 11, 1));
		
		Context.flushSession();
		
		List<Integer> ptIds = Arrays.asList(6, 7);
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new LastWhoStageCalculation());
		Assert.assertEquals(whoAdult1, ((Obs) resultMap.get(6).getValue()).getValueCoded());
		Assert.assertNull(resultMap.get(7)); // has no recorded stage
	}
}