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

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.CalculationProvider;
import org.openmrs.calculation.InvalidCalculationException;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.*;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.module.kenyaemr.regimen.RegimenDefinition;
import org.openmrs.module.kenyaemr.regimen.RegimenOrder;
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
	 * Extracts actual values from a list result. Always returns a list even if result is null.
	 * @param result the list result
	 * @param <T> the type of each value
	 * @return the list of values
	 */
	public static <T> List<T> extractListResultValues(ListResult result) {
		List<T> values = new ArrayList<T>();
		if (result != null) {
			for (SimpleResult resultItem : (List<SimpleResult>) result.getValue()) {
				values.add((T)resultItem.getValue());
			}
		}
		return values;
	}

	/**
	 * Evaluates the specified calculation for a single patient
	 * @param calculationClass the calculation class
	 * @param configuration the calculation configuration
	 * @param patientId the patient id
	 * @return th calculation result
	 * @throws InvalidCalculationException if no calculation with that name exists
	 */
	public static CalculationResult evaluateForPatient(Class <? extends BaseEmrCalculation> calculationClass, String configuration, Integer patientId) {
		BaseEmrCalculation calculation = CalculationManager.instantiateCalculation(calculationClass, configuration);
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

	/**
	 * Convenience method to fetch a patient result as an obs
	 * @param results the calculation result map
	 * @param patientId the patient id
	 * @return the obs result
	 */
	public static Obs obsResultForPatient(CalculationResultMap results, Integer patientId) {
		CalculationResult result = results.get(patientId);
		if (result != null && !result.isEmpty()) {
			Obs val = (Obs) result.getValue();
			return val;
		}
		return null;
	}

	/**
	 * Convenience method to fetch a patient result as a numeric obs value
	 * @param results the calculation result map
	 * @param patientId the patient id
	 * @return the numeric obs value
	 */
	public static Double numericObsResultForPatient(CalculationResultMap results, Integer patientId) {
		Obs o = obsResultForPatient(results, patientId);
		return o == null ? null : o.getValueNumeric();
	}

	/**
	 * Convenience method to fetch a patient result as a coded obs value
	 * @param results the calculation result map
	 * @param patientId the patient id
	 * @return the coded obs value
	 */
	public static Concept codedObsResultForPatient(CalculationResultMap results, Integer patientId) {
		Obs o = obsResultForPatient(results, patientId);
		return o == null ? null : o.getValueCoded();
	}

	/**
	 * Convenience method to fetch a patient result as a datetime obs value
	 * @param results the calculation result map
	 * @param patientId the patient id
	 * @return the datetime obs value
	 */
	public static Date datetimeObsResultForPatient(CalculationResultMap results, Integer patientId) {
		Obs o = obsResultForPatient(results, patientId);
		return o == null ? null : o.getValueDatetime();
	}

	/**
	 * Convenience method to fetch a patient result as an encounter
	 * @param results the calculation result map
	 * @param patientId the patient id
	 * @return the encounter result
	 */
	public static Encounter encounterResultForPatient(CalculationResultMap results, Integer patientId) {
		CalculationResult result = results.get(patientId);
		if (result != null && !result.isEmpty()) {
			return (Encounter) result.getValue();
		}
		return null;
	}

	/**
	 * Convenience method to fetch a patient result as a date
	 * @param results the calculation result map
	 * @param patientId the patient id
	 * @return the date result
	 */
	public static Date datetimeResultForPatient(CalculationResultMap results, Integer patientId) {
		CalculationResult result = results.get(patientId);
		if (result != null && !result.isEmpty()) {
			return (Date) result.getValue();
		}
		return null;
	}

	/**
	 * Checks if a regimen order matches a definition in a regimen group
	 * @param order the regimen order
	 * @param category the regimen category code
	 * @param groupCode the regimen group code
	 * @return true if regimen matches any definition in the given group
	 */
	public static boolean regimenInGroup(RegimenOrder order, String category, String groupCode) {
		List<RegimenDefinition> matchingDefinitions = KenyaEmr.getInstance().getRegimenManager().findDefinitions(category, order, false);
		for (RegimenDefinition definition : matchingDefinitions) {
			if (groupCode.equals(definition.getGroup().getCode())) {
				return true;
			}
		}
		return false;
	}
}