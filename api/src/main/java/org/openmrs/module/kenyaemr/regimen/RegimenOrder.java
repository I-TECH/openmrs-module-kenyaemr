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

import java.util.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.util.OpenmrsUtil;

/**
 * Represents a regimen of drug orders
 */
public class RegimenOrder {
	
	private Set<DrugOrder> drugOrders;

	/**
	 * Constructs an empty regimen
	 */
	public RegimenOrder() {
		this.drugOrders = new HashSet<DrugOrder>();
	}

	/**
	 * Constructs a regimen
	 * @param drugOrders the drug orders
	 */
	public RegimenOrder(Set<DrugOrder> drugOrders) {
		this.drugOrders = drugOrders;
	}
	
	/**
	 * @return the drugOrders
	 */
	public Set<DrugOrder> getDrugOrders() {
		return drugOrders;
	}
	
	/**
	 * Gets the drug orders in this regimen with the given concept
	 * @param genericDrug the concept
	 * @return the drug orders
	 */
	public List<DrugOrder> getDrugOrders(Concept genericDrug) {
		List<DrugOrder> ret = new ArrayList<DrugOrder>();
		for (DrugOrder o : drugOrders) {
			if (o.getConcept().equals(genericDrug)) {
				ret.add(o);
			}
		}
		return ret;
	}
	
	/**
	 * Sets the drug orders
	 * @param drugOrders the drug orders
	 */
	public void setDrugOrders(Set<DrugOrder> drugOrders) {
		this.drugOrders = drugOrders;
	}

	/**
	 * Adds a drug order
	 * @param drugOrder the drug order
	 */
	public void addDrugOrder(DrugOrder drugOrder) {
		drugOrders.add(drugOrder);
	}

	/**
	 * Gets the start date.. which should be the same across all contained drug orders
	 * @return the start date
	 */
	public Date getStartDate() {
		Iterator<DrugOrder> orderIterator = drugOrders.iterator();
		return orderIterator.hasNext() ? orderIterator.next().getStartDate() : null;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("drugOrders", drugOrders).toString();
	}
}