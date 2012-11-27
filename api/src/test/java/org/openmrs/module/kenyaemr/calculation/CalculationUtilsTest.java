/*
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

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;

import java.util.Arrays;
import java.util.List;

public class CalculationUtilsTest {

	/**
	 * @see CalculationUtils#ensureNullResults(org.openmrs.calculation.result.CalculationResultMap, java.util.Collection)
	 */
	@Test
	public void ensureNullResults_shouldAddNullsForMissingPatients() {

		CalculationResultMap map = new CalculationResultMap();
		map.put(7, new BooleanResult(true, null));
		map.put(999, new BooleanResult(true, null));

		List<Integer> cohort = Arrays.asList(6, 7);
		CalculationUtils.ensureNullResults(map, cohort);

		// Map should now contain 3 patient ids with null for #6
		Assert.assertEquals(3, map.size());
		Assert.assertTrue(map.containsKey(6));
		Assert.assertEquals(null, map.get(6));
		Assert.assertTrue((Boolean) map.get(7).getValue());
		Assert.assertTrue((Boolean) map.get(999).getValue());
	}

	/**
	 * @see CalculationUtils#ensureEmptyListResults(org.openmrs.calculation.result.CalculationResultMap, java.util.Collection)
	 */
	@Test
	public void ensureEmptyListResults_shouldAddEmptyListsForMissingPatients() {

		CalculationResultMap map = new CalculationResultMap();
		map.put(7, new ListResult());
		map.put(999, new ListResult());

		List<Integer> cohort = Arrays.asList(6, 7);
		CalculationUtils.ensureEmptyListResults(map, cohort);

		// Map should now contain 3 patient ids with empty list for #6
		Assert.assertEquals(3, map.size());
		Assert.assertTrue(map.get(6) instanceof ListResult);
		Assert.assertTrue(map.get(7) instanceof ListResult);
		Assert.assertTrue(map.get(999) instanceof ListResult);
	}
}
