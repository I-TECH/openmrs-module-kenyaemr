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