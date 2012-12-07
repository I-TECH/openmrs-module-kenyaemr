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

	private boolean pediatric;

	private List<RegimenComponent> components = new ArrayList<RegimenComponent>();

	/**
	 * Creates a new regimen definition
	 * @param name the name
	 * @param pediatric whether it's pediatric
	 */
	public RegimenDefinition(String name, boolean pediatric) {
		this.name = name;
		this.pediatric = pediatric;
	}

	/**
	 * Gets the name
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets if this is a pediatric regimen
	 * @return true if pediatric
	 */
	public boolean isPediatric() {
		return pediatric;
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
	 */
	public void addComponent(int conceptId, double dose) {
		components.add(new RegimenComponent(conceptId, dose));
	}

	/**
	 * RegimenComponent of a regimen (drug and dose)
	 */
	public class RegimenComponent {
		private int conceptId;
		private double dose;

		/**
		 * Creates a new component
		 * @param conceptId the concept id
		 * @param dose the dose
		 */
		public RegimenComponent(int conceptId, double dose) {
			this.conceptId = conceptId;
			this.dose = dose;
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
	}
}