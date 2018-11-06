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

/**
 * Represents predefined regimen with a name and group
 */
public class RegimenDefinition extends Regimen {

	private String name;
	private String conceptRef; // reference to concept for the regimen in CIEL dictionary

	private RegimenDefinitionGroup group;

	/**
	 * Creates a new regimen definition
	 * @param name the name
	 * @param group the group
	 */
	public RegimenDefinition(String name, RegimenDefinitionGroup group) {
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
	 * gets the reference concept uuid
	 * @return
	 */
	public String getConceptRef() {
		return conceptRef;
	}

	public void setConceptRef(String conceptRef) {
		this.conceptRef = conceptRef;
	}

	/**
	 * Gets the group
	 * @return the group
	 */
	public RegimenDefinitionGroup getGroup() {
		return group;
	}

	/**
	 * Convenience method to add a component
	 * @param drugRef the component drug reference
	 * @param dose the component dose
	 * @param units the component units
	 * @param frequency the component frequency
	 */
	public void addComponent(DrugReference drugRef, Double dose, Concept units, Concept frequency) {
		components.add(new RegimenComponent(drugRef, dose, units, frequency));
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("name", name).append("group", group).append("components", components).toString();
	}
}