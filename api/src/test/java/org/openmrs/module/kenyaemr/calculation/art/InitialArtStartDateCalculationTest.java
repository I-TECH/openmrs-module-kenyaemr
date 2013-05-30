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
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

public class InitialArtStartDateCalculationTest extends BaseModuleContextSensitiveTest {

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");
	}

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.art.InitialArtStartDateCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateInitialArtStartDate() throws Exception {

		PatientService ps = Context.getPatientService();
		Concept aspirin = Context.getConceptService().getConcept(71617);
		Concept azt = Context.getConceptService().getConcept(86663);
		Concept _3tc = Context.getConceptService().getConcept(78643);
		Concept efv = Context.getConceptService().getConcept(75523);

		// Put patient #6 on Aspirin
		TestUtils.saveDrugOrder(ps.getPatient(6), aspirin, TestUtils.date(2011, 1, 1), null);

		// Put patient #7 on AZT, then 3TC, then EFV
		TestUtils.saveDrugOrder(ps.getPatient(7), azt, TestUtils.date(2010, 1, 1), TestUtils.date(2011, 1, 1));
		TestUtils.saveDrugOrder(ps.getPatient(7), _3tc, TestUtils.date(2011, 1, 1), TestUtils.date(2012, 1, 1));
		TestUtils.saveDrugOrder(ps.getPatient(7), efv, TestUtils.date(2011, 1, 1), TestUtils.date(2012, 1, 1));

		Context.flushSession();
		
		List<Integer> cohort = Arrays.asList(2, 6, 7);

		CalculationResultMap resultMap = new InitialArtStartDateCalculation().evaluate(cohort, null, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertNull(resultMap.get(2)); // isn't on any drugs
		Assert.assertNull(resultMap.get(6)); // isn't on any ART drugs
		Assert.assertEquals(TestUtils.date(2010, 1, 1), resultMap.get(7).getValue());
	}
}