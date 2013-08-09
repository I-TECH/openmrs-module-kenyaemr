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

package org.openmrs.module.kenyacore.calculation;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyautil.test.TestUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Tests for {@link org.openmrs.module.kenyacore.calculation.CalculationUtils}
 */
public class CalculationUtilsTest {

	/**
	 * @see org.openmrs.module.kenyacore.calculation.CalculationUtils#dateAddDays(java.util.Date, int)
	 * @verifies shift the date by the number of days
	 */
	@Test
	public void dateAddDays_shouldShiftDateByNumberOfDays() {
		Assert.assertEquals(TestUtils.date(2012, 1, 2), CalculationUtils.dateAddDays(TestUtils.date(2012, 1, 1), 1));
		Assert.assertEquals(TestUtils.date(2012, 2, 1), CalculationUtils.dateAddDays(TestUtils.date(2012, 1, 1), 31));
		Assert.assertEquals(TestUtils.date(2011, 12, 31), CalculationUtils.dateAddDays(TestUtils.date(2012, 1, 1), -1));
	}

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

	/**
	 * @see CalculationUtils#extractListResultValues(org.openmrs.calculation.result.ListResult)
	 */
	@Test
	public void extractListResultValues_shouldExtractListResultValues() {
		// Test with empty list
		ListResult emptyList = new ListResult();
		List<Object> emptyValues = CalculationUtils.extractListResultValues(emptyList);
		Assert.assertEquals(0, emptyValues.size());

		// Test with non-empty list
		ListResult result = new ListResult();
		result.add(new SimpleResult(100, null));
		result.add(new SimpleResult(200, null));
		List<Integer> numericValues = CalculationUtils.extractListResultValues(result);
		Assert.assertEquals(2, numericValues.size());
		Assert.assertEquals(new Integer(100), numericValues.get(0));
		Assert.assertEquals(new Integer(200), numericValues.get(1));
	}

	/**
	 * @see CalculationUtils#earliestDate(java.util.Date, java.util.Date)
	 * @verifies return null if both dates are null
	 */
	@Test
	public void earliestDate_shouldReturnNullIfBothDatesAreNull() {
		Assert.assertNull(CalculationUtils.earliestDate(null, null));
	}

	/**
	 * @see CalculationUtils#earliestDate(java.util.Date, java.util.Date)
	 * @verifies return non-null date if one date is null
	 */
	@Test
	public void earliestDate_shouldReturnNonNullIfOneDateIsNull() {
		Date date = TestUtils.date(2001, 3, 22);
		Assert.assertEquals(date, CalculationUtils.earliestDate(null, date));
		Assert.assertEquals(date, CalculationUtils.earliestDate(date, null));
	}

	/**
	 * @see CalculationUtils#earliestDate(java.util.Date, java.util.Date)
	 * @verifies return earliest date of two non-null dates
	 */
	@Test
	public void earliestDate_shouldReturnEarliestDateOfTwoNonNullDates() {
		Date date1 = TestUtils.date(2001, 3, 22);
		Date date2 = TestUtils.date(2010, 2, 16);
		Assert.assertEquals(date1, CalculationUtils.earliestDate(date1, date2));
		Assert.assertEquals(date1, CalculationUtils.earliestDate(date2, date1));
	}
	//tests for latest obs date
	/**
	 * @see CalculationUtils#latestDate(java.util.Date, java.util.Date)
	 * @verifies return null if both dates are null
	 */
	@Test
	public void latestDate_shouldReturnNullIfBothDatesAreNull() {
		Assert.assertNull(CalculationUtils.latestDate(null, null));
	}
	/**
	 * @see CalculationUtils#latestDate(java.util.Date, java.util.Date)
	 * @verifies return non-null date if one date is null
	 */
	@Test
	public void latestDate_shouldReturnNonNullIfOneDateIsNull() {
		Date date = TestUtils.date(2010, 11, 22);
		Assert.assertEquals(date, CalculationUtils.latestDate(null, date));
		Assert.assertEquals(date, CalculationUtils.latestDate(date, null));
	}
	/**
	 * @see CalculationUtils#latestDate(java.util.Date, java.util.Date)
	 * @verifies return latest date of two non-null dates
	 */
	@Test
	public void latestDate_shouldReturnLateststDateOfTwoNonNullDates() {
		Date date1 = TestUtils.date(2010, 11, 22);
		Date date2 = TestUtils.date(2011, 2, 20);
		Assert.assertEquals(date2, CalculationUtils.latestDate(date1, date2));
		Assert.assertEquals(date2, CalculationUtils.latestDate(date2, date1));
	}
}