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

import org.openmrs.DrugOrder;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.CalculationProvider;
import org.openmrs.calculation.InvalidCalculationException;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.*;
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

	/**
	 * Extracts actual values from a list result
	 * @param result the list result
	 * @param <T> the type of each value
	 * @return the list of values
	 */
	public static <T> List<T> extractListResultValues(ListResult result) {
		List<T> values = new ArrayList<T>();
		for (SimpleResult resultItem : (List<SimpleResult>) result.getValue()) {
			values.add((T)resultItem.getValue());
		}
		return values;
	}

	/**
	 * Evaluates the specified calculation for a single patient
	 * @param provider the calculation provider
	 * @param name the calculation name
	 * @param configuration the calculation configuration
	 * @param patientId the patient id
	 * @return th calculation result
	 * @throws InvalidCalculationException if no calculation with that name exists
	 */
	public static CalculationResult evaluateForPatient(CalculationProvider provider, String name, String configuration, Integer patientId) throws InvalidCalculationException {
		BaseKenyaEmrCalculation calculation = (BaseKenyaEmrCalculation)provider.getCalculation(name, configuration);
		return Context.getService(PatientCalculationService.class).evaluate(patientId, calculation);
	}

	/**
	 * Calculates the earliest date of two given dates, ignoring null values
	 * @param d1 the first date
	 * @param d2 the second date
	 * @return the earliest date value
	 * @should return null if both dates are null
	 * @should return non-null date if one date is null
	 * @should return earliest date of two non-null dates
	 */
	public static Date earliestDate(Date d1, Date d2) {
		return OpenmrsUtil.compareWithNullAsLatest(d1, d2) >= 0 ? d2 : d1;
	}
}