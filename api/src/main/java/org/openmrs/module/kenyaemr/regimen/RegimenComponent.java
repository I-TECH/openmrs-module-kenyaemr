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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.OrderFrequency;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.CoreConstants;
import org.openmrs.util.OpenmrsConstants;

import java.util.Date;

/**
 * RegimenComponent of a regimen (drug, dose, units and frequency)
 */
public class RegimenComponent {

	private DrugReference drugRef;

	private Double dose;

	private Concept units;

	private Concept frequency;

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
	public RegimenComponent(DrugReference drugRef, Double dose, Concept units, Concept frequency) {
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
	public Concept getUnits() {
		return units;
	}

	/**
	 * Sets the units
	 * @param units the units
	 */
	public void setUnits(Concept units) {
		this.units = units;
	}

	/**
	 * Gets the frequency
	 * @return the frequency
	 */
	public Concept getFrequency() {
		return frequency;
	}

	/**
	 * Sets the frequency
	 * @param frequency the frequency
	 */
	public void setFrequency(Concept frequency) {
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
		OrderFrequency orderFrequency = new OrderFrequency();
		orderFrequency.setConcept(frequency);
		orderFrequency.setFrequencyPerDay(dose);
		order.setOrderType(Context.getOrderService().getOrderType(2));
		order.setPatient(patient);
		order.setDateActivated(start);
		order.setConcept(drugRef.getConcept());
		order.setDrug(drugRef.getDrug());
		order.setDose(dose);
		order.setDurationUnits(units);
		order.setFrequency(orderFrequency);
		return order;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("drugRef", drugRef).append("dose", dose).append("units", units.getShortNameInLocale(CoreConstants.LOCALE).getName()).append("frequency", frequency).toString();
	}
}