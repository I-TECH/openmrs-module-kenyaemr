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

/**
 * RegimenComponent of a regimen (drug, dose, units and frequency)
 */
public class RegimenComponent {

	private Integer conceptId;

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
	 * @param conceptId the concept id
	 * @param dose the dose, e.g. 200
	 * @param units the units, e.g. mg
	 * @apram frequency the frequency, e.g. OD
	 */
	public RegimenComponent(int conceptId, Double dose, String units, String frequency) {
		this.conceptId = conceptId;
		this.dose = dose;
		this.units = units;
		this.frequency = frequency;
	}

	/**
	 * Gets the concept id
	 * @return the concept id
	 */
	public Integer getConceptId() {
		return conceptId;
	}

	/**
	 * Sets the concept id
	 * @param conceptId the concept id
	 */
	public void setConceptId(Integer conceptId) {
		this.conceptId = conceptId;
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
		return conceptId != null && dose != null && units != null && frequency != null;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("conceptId", conceptId).append("dose", dose).append("units", units).append("frequency", frequency).toString();
	}
}