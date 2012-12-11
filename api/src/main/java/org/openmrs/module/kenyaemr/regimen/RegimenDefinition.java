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

/**
 * Represents a regimen of drugs that a patient could be prescribed
 */
public class RegimenDefinition {

	private String name;

	private String group;

	private List<RegimenComponent> components = new ArrayList<RegimenComponent>();

	/**
	 * Creates a new regimen definition
	 * @param name the name
	 * @param group the group
	 */
	public RegimenDefinition(String name, String group) {
		this.name = name;
		this.group = group;
	}

	/**
	 * Gets the name
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the group
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * Gets the components
	 * @return the regimen components
	 */
	public List<RegimenComponent> getComponents() {
		return components;
	}

	/**
	 * Adds a component
	 * @param conceptId the component concept id
	 * @param dose the component dose
	 * @param units the component units
	 * @param frequency the component frequency
	 */
	public void addComponent(int conceptId, Double dose, String units, String frequency) {
		components.add(new RegimenComponent(conceptId, dose, units, frequency));
	}

	/**
	 * RegimenComponent of a regimen (drug, dose, units and frequency)
	 */
	public class RegimenComponent {
		private int conceptId;
		private Double dose;
		private String units;
		private String frequency;

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
		public int getConceptId() {
			return conceptId;
		}

		/**
		 * Gets the dose
		 * @return the dose
		 */
		public Double getDose() {
			return dose;
		}

		/**
		 * Gets the units
		 * @return the units
		 */
		public String getUnits() {
			return units;
		}

		/**
		 * Gets the frequency
		 * @return the frequency
		 */
		public String getFrequency() {
			return frequency;
		}
	}
}