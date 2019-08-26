/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.regimen;

import org.openmrs.*;
import org.openmrs.CareSetting;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.util.EncounterBasedRegimenUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.Patient;

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
		CareSetting outpatient = Context.getOrderService().getCareSettingByName("OUTPATIENT");
		List<DrugOrder> drugOrdersOnly = EmrUtils.drugOrdersFromOrders(patient, outpatient);

		return new RegimenChangeHistory(relevantGenerics, drugOrdersOnly);
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
			tempChanges.add(new DrugOrderChange(ChangeType.START, o, o.getDateActivated()));
			if (o.getDateStopped() != null) {
				tempChanges.add(new DrugOrderChange(ChangeType.END, o, o.getDateStopped()));
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
					if (o.getOrderReason() != null)
						changeReasons.add(o.getOrderReason());
					if (o.getOrderReasonNonCoded() != null)
						changeReasonsNonCoded.add(o.getOrderReasonNonCoded());
					runningOrders.remove(o);
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
	public void undoLastChange(@FragmentParam Patient patient, @FragmentParam String category) {
		// Patient patient = Context.getPatientService().getPatientByUuid("a958ff30-0312-4fc6-8648-3fb00ffc23d9");
		EncounterService encounterService = Context.getEncounterService();
		Encounter lastEnc = EncounterBasedRegimenUtils.getLastEncounterForCategory(patient, category);
		if (lastEnc != null) {
			 encounterService.voidEncounter(lastEnc, "undo last regimen change");
		}
		/*RegimenChange lastChange = getLastChange();
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
				order.setAction(Order.Action.NEW); //order.setDiscontinued(false);
				//order.setDiscontinuedDate(null);
				//order.setDiscontinuedBy(null);
				//order.setDiscontinuedReason(null);
				//order.setDiscontinuedReasonNonCoded(null);


				Context.getOrderService().saveOrder(order, null);
			}
		}

		// Remove last change from history
		changes.remove(lastChange);*/
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