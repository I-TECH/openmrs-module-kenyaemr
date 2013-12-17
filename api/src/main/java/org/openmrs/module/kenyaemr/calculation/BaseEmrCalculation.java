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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.BaseCalculation;
import org.openmrs.calculation.CalculationContext;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.*;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.calculation.BooleanResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.data.patient.definition.DrugOrdersForPatientDataDefinition;

/**
 * Base class for calculations we'll hand-write for this module.
 */
public abstract class BaseEmrCalculation extends BaseCalculation implements PatientCalculation {

	/**
	 * Evaluates the active drug orders for each patient
	 * @param medSet the medset concept that specifies which drugs to include
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the drug orders in a calculation result map
	 */
	protected static CalculationResultMap activeDrugOrders(Concept medSet, Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		DrugOrdersForPatientDataDefinition def = new DrugOrdersForPatientDataDefinition("On " + medSet.getName().getName());
		def.setDrugConceptSetsToInclude(Collections.singletonList(medSet));
		def.setActiveOnDate(calculationContext.getNow());
		return CalculationUtils.evaluateWithReporting(def, cohort, null, null, calculationContext);
	}

	/**
	 * Evaluates all drug orders for each patient
	 * @param medSet the medset concept that specifies which drugs to include
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the drug orders in a calculation result map
	 */
	protected static CalculationResultMap allDrugOrders(Concept medSet, Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		DrugOrdersForPatientDataDefinition def = new DrugOrdersForPatientDataDefinition("First " + medSet.getName().getName() + " start date");
		def.setDrugConceptSetsToInclude(Collections.singletonList(medSet));
		def.setStartedOnOrBefore(calculationContext.getNow());
		return CalculationUtils.evaluateWithReporting(def, cohort, null, null, calculationContext);
	}

	/**
	 * Evaluates the first drug orders for each patient
	 * @param medSet the medset concept that specifies which drugs to include
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the drug orders in a calculation result map
	 */
	protected static CalculationResultMap firstDrugOrders(Concept medSet, Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		// Get all drug orders
		CalculationResultMap orders = allDrugOrders(medSet, cohort, calculationContext);

		// Calculate the earliest start date of any of the orders for each patient
		CalculationResultMap earliestStartDates = earliestStartDates(orders, calculationContext);

		// Return only the drug orders that start on the earliest date
		CalculationResultMap ret = new CalculationResultMap();
		for (Integer ptId : orders.keySet()) {
			ListResult allOrders = (ListResult) orders.get(ptId);
			ListResult earliestOrders = new ListResult();
			CalculationResult earliestDateResult = earliestStartDates.get(ptId);

			if (earliestDateResult != null) {
				Date earliestStartDate = (Date) earliestDateResult.getValue();

				for (SimpleResult r : (List<SimpleResult>) allOrders.getValue()) {
					DrugOrder order = (DrugOrder) r.getValue();
					if (order.getStartDate().equals(earliestStartDate)) {
						earliestOrders.add(new SimpleResult(order, null));
					}
				}
			}

			ret.put(ptId, earliestOrders);
		}
		return ret;
	}

	/**
	 * Filters a calculation result map to reduce results to booleans
	 * @param results the result map
	 * @return the reduced result map
	 */
	protected static CalculationResultMap passing(CalculationResultMap results) {
		CalculationResultMap ret = new CalculationResultMap();
		for (Map.Entry<Integer, CalculationResult> e : results.entrySet()) {
			ret.put(e.getKey(), new BooleanResult(ResultUtil.isTrue(e.getValue()), null));
		}
		return ret;
	}

	/**
	 * Evaluates the earliest start date of a set of drug orders
	 * @param orders the drug orders
	 * @param context the calculation context
	 * @return the start dates in a calculation result map
	 */
	protected static CalculationResultMap earliestStartDates(CalculationResultMap orders, PatientCalculationContext context) {
		CalculationResultMap ret = new CalculationResultMap();
		for (Map.Entry<Integer, CalculationResult> e : orders.entrySet()) {
			Integer ptId = e.getKey();
			ListResult result = (ListResult) e.getValue();
			Date earliest = null;

			if (result != null) {
				for (SimpleResult r : (List<SimpleResult>) result.getValue()) {
					Date candidate = ((DrugOrder) r.getValue()).getStartDate();
					earliest = CoreUtils.earliest(earliest, candidate);
				}
			}
			ret.put(ptId, earliest == null ? null : new SimpleResult(earliest, null));
		}
		return ret;
	}

	/**
	 * Evaluates a given calculation on each patient
	 * @param calculation the calculation
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the calculation result map
	 */
	protected static CalculationResultMap calculate(PatientCalculation calculation, Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		return Context.getService(PatientCalculationService.class).evaluate(cohort, calculation, calculationContext);
	}

	/**
	 * Extracts only alive patients from a cohort
	 * @param cohort the patient ids
	 * @param calculationContext the calculation context
	 * @return the extracted patient ids
	 */
	protected static Set<Integer> alivePatients(Collection<Integer> cohort, PatientCalculationContext calculationContext) {
		return CalculationUtils.patientsThatPass(Calculations.alive(cohort, calculationContext));
	}

	/**
	 * Convenience method to fetch a a concept by identifer
	 * @param identifier the concept identifier
	 * @return the concept
	 */
	protected static Concept getConcept(String identifier) {
		return MetadataUtils.getConcept(identifier);
	}

	/**
	 * Calculates the days since the given date
	 * @param date the date
	 * @param calculationContext the calculation context
	 * @return the number of days
	 */
	protected static int daysSince(Date date, CalculationContext calculationContext) {
		DateTime d1 = new DateTime(date.getTime());
		DateTime d2 = new DateTime(calculationContext.getNow().getTime());
		return Days.daysBetween(d1, d2).getDays();
	}
}