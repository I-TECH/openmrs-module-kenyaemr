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

package org.openmrs.module.kenyaemr.calculation.library;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

/**
 * Tests for {@link ScheduledVisitOnDayCalculation}
 */
public class ScheduledVisitOnDayCalculationTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		Concept returnVisitDate = Dictionary.getConcept(Dictionary.RETURN_VISIT_DATE);

		// Patient #7 has scheduled visit on 1-Jan-2012
		TestUtils.saveObs(TestUtils.getPatient(7), returnVisitDate, TestUtils.date(2012, 1, 1), TestUtils.date(2011, 12, 30));

		// Patient #8 has scheduled visit on 2-Jan-2012
		TestUtils.saveObs(TestUtils.getPatient(8), returnVisitDate, TestUtils.date(2012, 1, 2), TestUtils.date(2011, 12, 30));
	}

	/**
	 * @see ScheduledVisitOnDayCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateForSpecifiedDate() throws Exception {
		List<Integer> ptIds = Arrays.asList(6, 7, 8);
		Map<String, Object> paramValues = new HashMap<String, Object>();
		paramValues.put("date", TestUtils.date(2012, 1, 1));

		CalculationResultMap resultMap = new ScheduledVisitOnDayCalculation().evaluate(ptIds, paramValues, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertFalse((Boolean) resultMap.get(6).getValue());
		Assert.assertTrue((Boolean) resultMap.get(7).getValue());
		Assert.assertFalse((Boolean) resultMap.get(8).getValue());
	}
	/**
	 * @see ScheduledVisitOnDayCalculation#evaluate(java.util.Collection, java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Test
	public void evaluate_shouldCalculateForTodayWithoutDateParam() throws Exception {
		List<Integer> ptIds = Arrays.asList(6, 7, 8);
		Map<String, Object> paramValues = new HashMap<String, Object>();

		CalculationResultMap resultMap = new ScheduledVisitOnDayCalculation().evaluate(ptIds, paramValues, Context.getService(PatientCalculationService.class).createCalculationContext());
		Assert.assertFalse((Boolean) resultMap.get(6).getValue());
		Assert.assertFalse((Boolean) resultMap.get(7).getValue());
		Assert.assertFalse((Boolean) resultMap.get(8).getValue());
	}

}