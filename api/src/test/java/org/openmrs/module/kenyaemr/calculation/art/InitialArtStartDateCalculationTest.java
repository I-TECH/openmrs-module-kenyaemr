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
package org.openmrs.module.kenyaemr.calculation.art;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class InitialArtStartDateCalculationTest extends BaseModuleContextSensitiveTest {

	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("org/openmrs/module/kenyaemr/include/testData.xml");
	}

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.art.InitialArtStartDateCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateInitialArtStartDate() throws Exception {

		// Put patient #7 on Aspirin
		Concept aspirin = Context.getConceptService().getConcept(71617);
		TestUtils.saveDrugOrder(Context.getPatientService().getPatient(7), aspirin, TestUtils.date(2011, 1, 1), null);

		// Put patient #8 and #999 on Stavudine
		Concept stavudine = Context.getConceptService().getConcept(84309);
		TestUtils.saveDrugOrder(Context.getPatientService().getPatient(8), stavudine, TestUtils.date(2011, 1, 1), null);
		TestUtils.saveDrugOrder(Context.getPatientService().getPatient(999), stavudine, TestUtils.date(2011, 1, 1), null);

		// Give patient #999 an earlier date in a recent START DATE obs
		Concept arvStartDate = Context.getConceptService().getConcept(159599);
		TestUtils.saveObs(Context.getPatientService().getPatient(999), arvStartDate, TestUtils.date(2007, 7, 7), new Date());

		Context.flushSession();
		
		List<Integer> cohort = Arrays.asList(6, 7, 8, 999);

		CalculationResultMap resultMap = new InitialArtStartDateCalculation().evaluate(cohort, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertNull(resultMap.get(6)); // isn't on any drugs
		Assert.assertNull(resultMap.get(7)); // isn't on any ARTs

		Assert.assertEquals(TestUtils.date(2011, 1, 1), resultMap.get(8).getValue());
		Assert.assertEquals(TestUtils.date(2007, 7, 7), resultMap.get(999).getValue());
	}
}