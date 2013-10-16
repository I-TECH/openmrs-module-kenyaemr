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


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.openmrs.DrugOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Represents a regimen of drug orders
 */
public class RegimenOrder {
	
	private Set<DrugOrder> drugOrders;

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
	 * Gets the drug orders in this regimen that match the given drug reference
	 * @param drugRef the drug reference
	 * @return the drug orders
	 */
	public List<DrugOrder> getDrugOrders(DrugReference drugRef) {
		List<DrugOrder> ret = new ArrayList<DrugOrder>();
		for (DrugOrder o : drugOrders) {
			if (DrugReference.fromDrugOrder(o).equals(drugRef)) {
				ret.add(o);
			}
		}
		return ret;
	}

	/**
	 * Gets the drugs in this regimen as references
	 * @return the drugOrders
	 */
	public List<DrugReference> getDrugReferences() {
		List<DrugReference> drugs = new ArrayList<DrugReference>();
		for (DrugOrder order : drugOrders) {
			drugs.add(DrugReference.fromDrugOrder(order));
		}
		return drugs;
	}

	/**
	 * Checks the equality of regimen orders considering drugs only
	 * @param order the other order
	 * @return true if the order has same drugs as this order
	 */
	public boolean hasSameDrugs(RegimenOrder order) {
		if (order == null) {
			return false;
		}

		return CollectionUtils.isEqualCollection(getDrugReferences(), order.getDrugReferences());
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RegimenOrder)) {
			return false;
		}

		RegimenOrder order = (RegimenOrder) o;

		return drugOrders.equals(order.getDrugOrders());
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return drugOrders.hashCode();
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("drugOrders", drugOrders).toString();
	}
}