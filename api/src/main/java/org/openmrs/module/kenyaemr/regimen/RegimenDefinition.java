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

	/**
	 * Represents the age suitability of a regimen
	 */
	public enum Suitability {
		INFANT, CHILD, ADULT;

		/**
		 * Parses a string into a suitability value
		 * @param val the string
		 * @return the suitability
		 */
		public static Suitability parse(String val) {
			return val != null ? Suitability.valueOf(val.trim().toUpperCase()) : null;
		}
	}

	private String name;

	private Suitability suitability;

	private List<RegimenComponent> components = new ArrayList<RegimenComponent>();

	/**
	 * Creates a new regimen definition
	 * @param name the name
	 * @param suitability the suitability
	 */
	public RegimenDefinition(String name, Suitability suitability) {
		this.name = name;
		this.suitability = suitability;
	}

	/**
	 * Gets the name
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the suitability
	 * @return the suitability
	 */
	public Suitability getSuitability() {
		return suitability;
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
	 */
	public void addComponent(int conceptId, double dose, String units) {
		components.add(new RegimenComponent(conceptId, dose, units));
	}

	/**
	 * RegimenComponent of a regimen (drug, dose and units)
	 */
	public class RegimenComponent {
		private int conceptId;
		private double dose;
		private String units;

		/**
		 * Creates a new component
		 * @param conceptId the concept id
		 * @param dose the dose
		 * @param units the units
		 */
		public RegimenComponent(int conceptId, double dose, String units) {
			this.conceptId = conceptId;
			this.dose = dose;
			this.units = units;
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
		public double getDose() {
			return dose;
		}

		/**
		 * Gets the units
		 * @return the units
		 */
		public String getUnits() {
			return units;
		}

	}
}