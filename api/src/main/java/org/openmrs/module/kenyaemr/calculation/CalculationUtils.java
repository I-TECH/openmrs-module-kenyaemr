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

import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.ResultUtil;
import org.openmrs.util.OpenmrsUtil;

import java.util.*;

/**
 * Calculation utility methods, also used by some reporting classes
 */
public class CalculationUtils {

	/**
	 * Extracts patients from calculation result map with non-false/empty results
	 * @param results calculation result map
	 * @return the extracted patient ids
	 */
	public static Set<Integer> patientsThatPass(CalculationResultMap results) {
		Set<Integer> ret = new HashSet<Integer>();
		for (Map.Entry<Integer, CalculationResult> e : results.entrySet()) {
			if (ResultUtil.isTrue(e.getValue())) {
				ret.add(e.getKey());
			}
		}
		return ret;
	}

	/**
	 * Extracts patients from calculation result map with false/empty results
	 * @param results the calculation result map
	 * @return the extracted patient ids
	 */
	public static Set<Integer> patientsThatDoNotPass(CalculationResultMap results) {
		Set<Integer> ret = new HashSet<Integer>();
		for (Map.Entry<Integer, CalculationResult> e : results.entrySet()) {
			if (ResultUtil.isFalse(e.getValue())) {
				ret.add(e.getKey());
			}
		}
		return ret;
	}

	/**
	 * Extracts patients from a calculation result map with date results in the given range
	 * @param results the calculation result map
	 * @param minDateInclusive the minimum date (inclusive)
	 * @param maxDateInclusive the maximum date (inclusive)
	 * @return the extracted patient ids
	 */
	public static Set<Integer> datesWithinRange(CalculationResultMap results, Date minDateInclusive, Date maxDateInclusive) {
		Set<Integer> ret = new HashSet<Integer>();
		for (Map.Entry<Integer, CalculationResult> e : results.entrySet()) {
			Date result = null;
			try {
				result = e.getValue().asType(Date.class);
			} catch (Exception ex) {
				// pass
			}
			if (result != null) {
				if (OpenmrsUtil.compareWithNullAsEarliest(result, minDateInclusive) >= 0 &&
						OpenmrsUtil.compareWithNullAsLatest(result, maxDateInclusive) <= 0) {
					ret.add(e.getKey());
				}
			}
		}
		return ret;
	}

	/**
	 * Ensures all patients exist in a result map. If map is missing entries for any of patientIds, they are added with a null result
	 * @param map the calculation result map
	 * @param cohort the patient ids
	 */
	public static void ensureNullResults(CalculationResultMap map, Collection<Integer> cohort) {
		for (Integer ptId : cohort) {
			if (!map.containsKey(ptId)) {
				map.put(ptId, null);
			}
		}
	}

	/**
	 * Ensures all patients exist in a result map. If map is missing entries for any of patientIds, they are added with an empty list result
	 * @param map the calculation result map
	 * @param cohort the patient ids
	 */
	public static void ensureEmptyListResults(CalculationResultMap map, Collection<Integer> cohort) {
		for (Integer ptId : cohort) {
			if (!map.containsKey(ptId)) {
				map.put(ptId, new ListResult());
			}
		}
	}
}