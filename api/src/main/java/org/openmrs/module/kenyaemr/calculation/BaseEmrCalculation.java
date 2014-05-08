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

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.reporting.data.patient.definition.DrugOrdersForPatientDataDefinition;

/**
 * Previously the base class for calculations, replaced by AbstractPatientCalculation in KenyaCore.
 *
 * This class is deprecated because it now only contains drug order related functionality which should be moved into a
 * different class, and also may not work with OpenMRS 1.10
 */
@Deprecated
public abstract class BaseEmrCalculation extends AbstractPatientCalculation {

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
}