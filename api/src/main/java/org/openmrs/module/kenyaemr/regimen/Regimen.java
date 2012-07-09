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
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.openmrs.DrugOrder;
import org.openmrs.util.OpenmrsUtil;

/**
 *
 */
public class Regimen {
	
	private Set<DrugOrder> drugOrders;
	
	public Regimen(Set<DrugOrder> drugOrders) {
		this.drugOrders = drugOrders;
	}
	
	/**
	 * @return the drugOrders
	 */
	public Set<DrugOrder> getDrugOrders() {
		return drugOrders;
	}
	
	/**
	 * @param drugOrders the drugOrders to set
	 */
	public void setDrugOrders(Set<DrugOrder> drugOrders) {
		this.drugOrders = drugOrders;
	}
	
	public String toString() {
		List<String> list = new ArrayList<String>();
		for (DrugOrder o : drugOrders) {
			list.add(o.getConcept().getPreferredName(Locale.ENGLISH).getName());
		}
		return OpenmrsUtil.join(list, ", ");
	}
	
}
