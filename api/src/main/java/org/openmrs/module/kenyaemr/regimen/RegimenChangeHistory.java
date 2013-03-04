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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;

/**
 * Regimen change history of a patient. Use for ARVs
 */
public class RegimenChangeHistory {

	private enum ChangeType {
		START, END
	}

	private List<RegimenChange> changes;

	/**
	 * Generates a regimen order history for the given patient
	 * @param patient the patient
	 * @param medSet the medset concept defining the list of relevant drug concepts
	 * @return the regimen history
	 */
	public static RegimenChangeHistory forPatient(Patient patient, Concept medSet) {
		Set<Concept> relevantGenerics = new HashSet<Concept>(medSet.getSetMembers());
		@SuppressWarnings("deprecation")
		List<DrugOrder> allDrugOrders = Context.getOrderService().getDrugOrdersByPatient(patient);
		return new RegimenChangeHistory(relevantGenerics, allDrugOrders);
	}
	
	/**
	 * Constructs a regimen order history
	 * @param relevantDrugs
	 * @param allDrugOrders
	 * @should create regimen history based on drug orders
	 */
	protected RegimenChangeHistory(Set<Concept> relevantDrugs, List<DrugOrder> allDrugOrders) {

		// Filter the drug orders to only contain orders of relevant drugs
		List<DrugOrder> relevantDrugOrders = filterByConcepts(allDrugOrders, relevantDrugs);

		// Collect changes for each individual drug orders
		List<DrugOrderChange> tempChanges = new ArrayList<DrugOrderChange>();
		for (DrugOrder o : relevantDrugOrders) {
			tempChanges.add(new DrugOrderChange(ChangeType.START, o, o.getStartDate()));
			if (o.getDiscontinuedDate() != null) {
				tempChanges.add(new DrugOrderChange(ChangeType.END, o, o.getDiscontinuedDate()));
			} else if (o.getAutoExpireDate() != null) {
				tempChanges.add(new DrugOrderChange(ChangeType.END, o, o.getAutoExpireDate()));
			}
		}

		// Gather changes together by common dates
		SortedMap<Date, List<DrugOrderChange>> changesByDate = new TreeMap<Date, List<DrugOrderChange>>();
		for (DrugOrderChange change : tempChanges) {
			List<DrugOrderChange> holder = changesByDate.get(change.getDate());
			if (holder == null) {
				holder = new ArrayList<DrugOrderChange>();
				changesByDate.put(change.getDate(), holder);
			}
			holder.add(change);
		}

		// Group drug orders into regimens based on common change dates
		changes = new ArrayList<RegimenChange>();
		Set<DrugOrder> runningOrders = new LinkedHashSet<DrugOrder>();
		RegimenOrder lastRegimen = null;
		for (Map.Entry<Date, List<DrugOrderChange>> e : changesByDate.entrySet()) {
			Date date = e.getKey();
			Set<Concept> changeReasons = new LinkedHashSet<Concept>();
			Set<String> changeReasonsNonCoded = new LinkedHashSet<String>();
			for (DrugOrderChange rc : e.getValue()) {
				if (ChangeType.START.equals(rc.getType())) {
					runningOrders.add(rc.getDrugOrder());
				} else { // ChangeType.END
					DrugOrder o = rc.getDrugOrder();
					runningOrders.remove(o);
					if (o.getDiscontinuedReason() != null) {
						changeReasons.add(o.getDiscontinuedReason());
					}
					if (o.getDiscontinuedReasonNonCoded() != null) {
						changeReasonsNonCoded.add(o.getDiscontinuedReasonNonCoded());
					}
				}
			}

			// Construct new regimen if there are running drug orders
			RegimenOrder newRegimen = null;
			if (runningOrders.size() > 0) {
				newRegimen = new RegimenOrder(new LinkedHashSet<DrugOrder>(runningOrders));
			}

			RegimenChange change = new RegimenChange(date, lastRegimen, newRegimen, changeReasons, changeReasonsNonCoded);
			changes.add(change);
			lastRegimen = newRegimen;
		}
	}

	/**
	 * Undoes the last change in the history
	 */
	public void undoLastChange() {
		RegimenChange lastChange = getLastChange();
		if (lastChange == null) {
			return;
		}

		// Void the regimen that may have been started
		if (lastChange.getStarted() != null) {
			for (DrugOrder order : lastChange.getStarted().getDrugOrders()) {
				Context.getOrderService().voidOrder(order, "Undoing last regimen change");
			}
		}

		// Un-discontinue the regimen that may have been stopped
		if (lastChange.getStopped() != null) {
			for (DrugOrder order : lastChange.getStopped().getDrugOrders()) {
				order.setDiscontinued(false);
				order.setDiscontinuedDate(null);
				order.setDiscontinuedBy(null);
				order.setDiscontinuedReason(null);
				order.setDiscontinuedReasonNonCoded(null);
				Context.getOrderService().saveOrder(order);
			}
		}

		// Remove last change from history
		changes.remove(lastChange);
	}
	
	/**
	 * @return the changes
	 */
	public List<RegimenChange> getChanges() {
		return changes;
	}

	/**
	 * Convenience method to get the last change
	 * @return the last regimen change
	 */
	public RegimenChange getLastChange() {
		return (changes.size() > 0) ? changes.get(changes.size() - 1) : null;
	}

	/**
	 * Convenience method to get the last regimen change before now
	 * @return the regimen change
	 */
	public RegimenChange getLastChangeBeforeNow() {
		return getLastChangeBeforeDate(new Date());
	}
	
	/**
	 * Gets the last regimen change before the given date
	 * @return the regimen change
	 */
	public RegimenChange getLastChangeBeforeDate(Date date) {
		for (ListIterator<RegimenChange> i = changes.listIterator(changes.size()); i.hasPrevious();) {
			RegimenChange candidate = i.previous();
			if (OpenmrsUtil.compare(candidate.getDate(), date) <= 0) {
				return candidate;
			}
		}
		return null;
	}

	/**
	 * Helper class for tagging when a DrugOrder starts or stops
	 */
	private class DrugOrderChange {

		private Date date;

		private ChangeType type;

		private DrugOrder drugOrder;

		public DrugOrderChange(ChangeType type, DrugOrder drugOrder, Date date) {
			this.type = type;
			this.drugOrder = drugOrder;
			this.date = date;
		}

		/**
		 * @return the date
		 */
		public Date getDate() {
			return date;
		}

		/**
		 * @return the type
		 */
		public ChangeType getType() {
			return type;
		}

		/**
		 * @return the drugOrder
		 */
		public DrugOrder getDrugOrder() {
			return drugOrder;
		}
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
}