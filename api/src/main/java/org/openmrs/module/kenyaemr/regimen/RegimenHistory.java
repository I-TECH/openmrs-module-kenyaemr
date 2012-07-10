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
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.util.OpenmrsUtil;

/**
 *
 */
public class RegimenHistory {
	
	List<RegimenChange> changes;
	
	public static RegimenHistory forPatient(Patient patient, Concept medSet) {
		Set<Concept> relevantGenerics = new HashSet<Concept>(medSet.getSetMembers());
		@SuppressWarnings("deprecation")
		List<DrugOrder> allDrugOrders = Context.getOrderService().getDrugOrdersByPatient(patient);
		return new RegimenHistory(relevantGenerics, allDrugOrders);
	}
	
	/**
	 * @param relevantGenericDrugs
	 * @param allDrugOrders
	 * @should create regimen history based on drug orders
	 */
	public RegimenHistory(Set<Concept> relevantGenericDrugs, List<DrugOrder> allDrugOrders) {
		List<DrugOrder> relevantDrugOrders = new ArrayList<DrugOrder>();
		for (DrugOrder o : allDrugOrders) {
			if (relevantGenericDrugs.contains(o.getConcept())) {
				relevantDrugOrders.add(o);
			}
		}
		
		List<RegChange> tempChanges = new ArrayList<RegimenHistory.RegChange>();
		for (DrugOrder o : relevantDrugOrders) {
			tempChanges.add(new RegChange(ChangeType.START, o, o.getStartDate()));
			if (o.getDiscontinuedDate() != null) {
				tempChanges.add(new RegChange(ChangeType.END, o, o.getDiscontinuedDate()));
			} else if (o.getAutoExpireDate() != null) {
				tempChanges.add(new RegChange(ChangeType.END, o, o.getAutoExpireDate()));
			}
		}
		
		SortedMap<Date, List<RegChange>> changesByDate = new TreeMap<Date, List<RegChange>>();
		for (RegChange change : tempChanges) {
			List<RegChange> holder = changesByDate.get(change.getDate());
			if (holder == null) {
				holder = new ArrayList<RegimenHistory.RegChange>();
				changesByDate.put(change.getDate(), holder);
			}
			holder.add(change);
		}
		
		changes = new ArrayList<RegimenChange>();
		Set<DrugOrder> runningOrders = new LinkedHashSet<DrugOrder>();
		Regimen lastRegimen = null;
		for (Map.Entry<Date, List<RegChange>> e : changesByDate.entrySet()) {
			Date date = e.getKey();
			Set<Concept> changeReasons = new LinkedHashSet<Concept>();
			Set<String> changeReasonsNonCoded = new LinkedHashSet<String>();
			for (RegChange rc : e.getValue()) {
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
			Regimen newRegimen = new Regimen(new LinkedHashSet<DrugOrder>(runningOrders));
			RegimenChange change = new RegimenChange(date, lastRegimen, newRegimen, changeReasons, changeReasonsNonCoded);
			changes.add(change);
			lastRegimen = newRegimen;
		}
	}
	
	/**
	 * @return the changes
	 */
	public List<RegimenChange> getChanges() {
		return changes;
	}
	
	/**
	 * @param history
	 * @param ui
	 * @return a list of object with { startDate, endDate, shortDisplay, longDisplay,
	 *         changeReasons[] }
	 */
	public List<SimpleObject> asSimpleRegimenHistory(UiUtils ui) {
		List<RegimenChange> changes = getChanges();
		
		List<SimpleObject> ret = new ArrayList<SimpleObject>();
		
		if (changes.size() == 0) {
			return ret;
		}
		
		for (int i = 0; i < changes.size(); ++i) {
			RegimenChange change = changes.get(i);
			Date startDate = change.getDate();
			Regimen reg = change.getStarted();
			Date endDate = null;
			List<String> changeReasons = new ArrayList<String>();
			if (i + 1 < changes.size()) {
				RegimenChange next = changes.get(i + 1);
				endDate = next.getDate();
				if (next.getChangeReasons() != null) {
					for (Concept c : next.getChangeReasons()) {
						changeReasons.add(ui.format(c));
					}
				}
				if (next.getChangeReasonsNonCoded() != null) {
					changeReasons.addAll(next.getChangeReasonsNonCoded());
				}
			}
			ret.add(SimpleObject.create("startDate", ui.format(startDate), "endDate", ui.format(endDate), "shortDisplay",
			    reg.getShortDisplay(ui), "longDisplay", reg.getLongDisplay(ui), "changeReasons", changeReasons));
		}
		
		return ret;
	}
	
	/**
	 * @param changes the changes to set
	 */
	public void setChanges(List<RegimenChange> changes) {
		this.changes = changes;
	}
	
	private enum ChangeType {
		START, END
	}
	
	/**
	 * Helper class for tagging when a DrugOrder starts or stops
	 */
	private class RegChange {
		
		private Date date;
		
		private ChangeType type;
		
		private DrugOrder drugOrder;
		
		public RegChange(ChangeType type, DrugOrder drugOrder, Date date) {
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
	
	public Regimen getCurrentRegimen() {
		return getRegimenOnDate(new Date());
	}
	
	/**
	 * Gets the regimen on the specified date, or now() if null
	 */
	public Regimen getRegimenOnDate(Date date) {
		if (date == null) {
			date = new Date();
		}
		for (ListIterator<RegimenChange> i = changes.listIterator(changes.size()); i.hasPrevious();) {
			RegimenChange candidate = i.previous();
			if (OpenmrsUtil.compare(candidate.getDate(), date) <= 0) {
				return candidate.getStarted();
			}
		}
		return null;
	}
	
}
