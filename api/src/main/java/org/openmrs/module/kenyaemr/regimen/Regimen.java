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

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.DrugOrder;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.UiUtils;
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
	 * @param genericDrug
	 * @return drug orders whose concept is genericDrug
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
	
	/**
	 * @param ui
	 * @return short version of display string for the components of reg
	 */
	public String getShortDisplay(UiUtils ui) {
		if (CollectionUtils.isEmpty(getDrugOrders())) {
			return "None";
		}
		List<String> components = new ArrayList<String>();
		for (DrugOrder o : getDrugOrders()) {
			ConceptName cn = o.getConcept().getPreferredName(Context.getLocale());
			if (cn == null) {
				cn = o.getConcept().getName(Context.getLocale());
			}
			components.add(cn.getName());
		}
		return OpenmrsUtil.join(components, ", ");
	}
	
	/**
	 * @param ui
	 * @return long version of display string for the components of reg
	 */
	public String getLongDisplay(UiUtils ui) {
		if (CollectionUtils.isEmpty(getDrugOrders())) {
			return "None";
		}
		List<String> components = new ArrayList<String>();
		for (DrugOrder o : getDrugOrders()) {
			ConceptName cn = o.getConcept().getPreferredName(Context.getLocale());
			if (cn == null) {
				cn = o.getConcept().getName(Context.getLocale());
			}
			String s = cn.getName();
			if (o.getDose() != null) {
				s += " " + ui.format(o.getDose()) + o.getUnits();
			}
			components.add(s);
		}
		return OpenmrsUtil.join(components, ", ");
	}
	
}
