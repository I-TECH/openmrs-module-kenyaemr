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

package org.openmrs.module.kenyaemr.regimen;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;

import java.util.*;

/**
 * Regimen order history of a patient. Use for TB regimens
 */
public class RegimenOrderHistory {

	protected List<RegimenOrder> orders;

	/**
	 * Generates a regimen order history for the given patient
	 * @param patient the patient
	 * @param medSet the medset concept defining the list of relevant drug concepts
	 * @return the regimen history
	 */
	public static RegimenOrderHistory forPatient(Patient patient, Concept medSet) {
		Set<Concept> relevantGenerics = new HashSet<Concept>(medSet.getSetMembers());
		List<DrugOrder> allDrugOrders = Context.getOrderService().getDrugOrdersByPatient(patient);
		return new RegimenOrderHistory(relevantGenerics, allDrugOrders);
	}

	/**
	 * Constructs a regimen order history
	 * @param relevantDrugs the relevant drugs
	 * @param allDrugOrders the orders
	 * @should create regimen history based on drug orders
	 */
	protected RegimenOrderHistory(Set<Concept> relevantDrugs, List<DrugOrder> allDrugOrders) {
		// Filter the drug orders to only contain orders of relevant drugs
		List<DrugOrder> relevantDrugOrders = filterByConcepts(allDrugOrders, relevantDrugs);

		orders = new ArrayList<RegimenOrder>(groupDrugOrdersByDates(relevantDrugOrders));

		Collections.sort(orders);
	}

	/**
	 * Gets the regimen orders
	 * @return the regimen orders
	 */
	public List<RegimenOrder> getOrders() {
		return orders;
	}

	/**
	 * Convenience method to get the current regimen
	 * @return the regimen
	 */
	public List<RegimenOrder> getCurrentOrders() {
		return getOrdersOnDate(new Date());
	}

	/**
	 * Gets the regimen orders on the specified date
	 * @param date the date to check
	 */
	public List<RegimenOrder> getOrdersOnDate(Date date) {
		List<RegimenOrder> regimensOnDate = new ArrayList<RegimenOrder>();

		for (RegimenOrder regimenOrder : orders) {
			if (regimenOrder.isCurrent(date)) {
				regimensOnDate.add(regimenOrder);
			}
		}
		return regimensOnDate;
	}

	/**
	 * Filters a list of orders by a set of relevant concepts
	 * @param orders the orders
	 * @param concepts the relevant concepts
	 * @return the filtered list of orders
	 */
	protected static List<DrugOrder> filterByConcepts(List<DrugOrder> orders, Set<Concept> concepts) {
		List<DrugOrder> filteredOrders = new ArrayList<DrugOrder>();
		for (DrugOrder o : orders) {
			if (concepts.contains(o.getConcept())) {
				filteredOrders.add(o);
			}
		}
		return filteredOrders;
	}

	/**
	 * Groups drug orders into regimen orders based on their dates
	 * @param drugOrders the drug orders
	 * @return the regimen orders
	 */
	protected static Collection<RegimenOrder> groupDrugOrdersByDates(List<DrugOrder> drugOrders) {
		Map<Pair<Date, Date>, RegimenOrder> regimensByDate = new HashMap<Pair<Date, Date>, RegimenOrder>();
		for (DrugOrder order : drugOrders) {
			Date startDate = order.getStartDate();
			Date endDate = order.getDiscontinuedDate() != null ? order.getDiscontinuedDate() : order.getAutoExpireDate();
			Pair<Date, Date> orderDates = new ImmutablePair<Date, Date>(startDate, endDate);

			RegimenOrder regimen = regimensByDate.get(orderDates);

			if (regimen == null) {
				regimen = new RegimenOrder();
				regimensByDate.put(orderDates, regimen);
			}

			regimen.addDrugOrder(order);
		}

		return regimensByDate.values();
	}
}