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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsConstants;

import java.util.Date;

/**
 * RegimenComponent of a regimen (drug, dose, units and frequency)
 */
public class RegimenComponent {

	private DrugReference drugRef;

	private Double dose;

	private String units;

	private String frequency;

	/**
	 * Creates an empty component
	 */
	public RegimenComponent() {
	}

	/**
	 * Creates a new component
	 * @param drugRef the drug reference
	 * @param dose the dose, e.g. 200
	 * @param units the units, e.g. mg
	 * @apram frequency the frequency, e.g. OD
	 */
	public RegimenComponent(DrugReference drugRef, Double dose, String units, String frequency) {
		this.drugRef = drugRef;
		this.dose = dose;
		this.units = units;
		this.frequency = frequency;
	}

	/**
	 * Gets the drug reference
	 * @return the drug reference
	 */
	public DrugReference getDrugRef() {
		return drugRef;
	}

	/**
	 * Sets the drug reference
	 * @param drugRef the drug reference
	 */
	public void setDrugRef(DrugReference drugRef) {
		this.drugRef = drugRef;
	}

	/**
	 * Gets the dose
	 * @return the dose
	 */
	public Double getDose() {
		return dose;
	}

	/**
	 * Sets the dose
	 * @param dose the dose
	 */
	public void setDose(Double dose) {
		this.dose = dose;
	}

	/**
	 * Gets the units
	 * @return the units
	 */
	public String getUnits() {
		return units;
	}

	/**
	 * Sets the units
	 * @param units the units
	 */
	public void setUnits(String units) {
		this.units = units;
	}

	/**
	 * Gets the frequency
	 * @return the frequency
	 */
	public String getFrequency() {
		return frequency;
	}

	/**
	 * Sets the frequency
	 * @param frequency the frequency
	 */
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	/**
	 * Checks if this component is complete
	 * @return true if complete
	 */
	public boolean isComplete() {
		return drugRef != null && dose != null && units != null && frequency != null;
	}

	/**
	 * Creates a drug order from this component
	 * @return the drug order
	 */
	public DrugOrder toDrugOrder(Patient patient, Date start) {
		DrugOrder order = new DrugOrder();
		order.setOrderType(Context.getOrderService().getOrderType(OpenmrsConstants.ORDERTYPE_DRUG));
		order.setPatient(patient);
		order.setStartDate(start);
		order.setConcept(drugRef.getConcept());
		order.setDrug(drugRef.getDrug());
		order.setDose(dose);
		order.setUnits(units);
		order.setFrequency(frequency);
		return order;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("drugRef", drugRef).append("dose", dose).append("units", units).append("frequency", frequency).toString();
	}
}