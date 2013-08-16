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

package org.openmrs.module.kenyaemr.calculation.library.hiv;

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
import org.openmrs.module.kenyaemr.calculation.library.hiv.LastCd4PercentageCalculation;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.List;

public class LastCd4PercentageCalculationTest extends BaseModuleContextSensitiveTest {

	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("test-data.xml");
	}

	/**
	 * @see org.openmrs.module.kenyaemr.calculation.library.hiv.NeedsCd4TestCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 * @verifies calculate last CD4 percentage for all patients
	 */
	@Test
	public void evaluate_shouldCalculateLastCD4() throws Exception {

		PatientService ps = Context.getPatientService();
		Concept cd4Percent = Dictionary.getConcept(Dictionary.CD4_PERCENT);

		// Give patient #6 some CD4 obs
		TestUtils.saveObs(ps.getPatient(6), cd4Percent, 20d, TestUtils.date(2012, 12, 1));
		TestUtils.saveObs(ps.getPatient(6), cd4Percent, 30d, TestUtils.date(2010, 11, 1));
		
		Context.flushSession();
		
		List<Integer> ptIds = Arrays.asList(6, 999);
		CalculationResultMap resultMap = Context.getService(PatientCalculationService.class).evaluate(ptIds, new LastCd4PercentageCalculation());
		Assert.assertEquals(new Double(20d), ((Obs) resultMap.get(6).getValue()).getValueNumeric());
		Assert.assertNull(resultMap.get(999)); // has no recorded CD4 percent
	}
}