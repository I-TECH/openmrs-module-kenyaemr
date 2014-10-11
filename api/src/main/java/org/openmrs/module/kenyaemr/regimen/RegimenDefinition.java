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
 * Represents predefined regimen with a name and group
 */
public class RegimenDefinition extends Regimen {

	private String name;

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
	public void addComponent(DrugReference drugRef, Double dose, String units, String frequency) {
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