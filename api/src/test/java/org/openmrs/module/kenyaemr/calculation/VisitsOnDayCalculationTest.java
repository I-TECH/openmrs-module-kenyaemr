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
package org.openmrs.module.kenyaemr.calculation;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ResultUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;


/**
 *
 */
public class VisitsOnDayCalculationTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("org/openmrs/module/kenyaemr/include/testData.xml");
	}
	
	@Test
	public void shouldCalculateWithScheduledVisit() throws Exception {
		List<Integer> ptIds = Arrays.asList(6, 7, 8);
		Map<String, Object> paramValues = new HashMap<String, Object>();
		paramValues.put("date", new SimpleDateFormat("yyyy-MM-dd").parse("2012-07-04"));
		CalculationResultMap resultMap = new VisitsOnDayCalculation().evaluate(ptIds, paramValues, Context.getService(PatientCalculationService.class).createCalculationContext());
		
		for (Integer ptId : resultMap.keySet()) {
			System.out.println(ptId + " -> " + ResultUtil.getFirst(resultMap.get(ptId)));
		}
		
		Assert.assertTrue(resultMap.get(6).isEmpty());
		Assert.assertFalse(resultMap.get(7).isEmpty());
		Assert.assertTrue(resultMap.get(8).isEmpty());
	}
	
}
