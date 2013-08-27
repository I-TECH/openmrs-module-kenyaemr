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
 * A group of regimen definitions, e.g. adult-first line ARVs
 */
public class RegimenDefinitionGroup {

	private String code;

	private String name;

	private List<RegimenDefinition> regimens = new ArrayList<RegimenDefinition>();

	/**
	 * Creates a new regimen group
	 * @param code the code
	 * @param name the name
	 */
	public RegimenDefinitionGroup(String code, String name) {
		this.code = code;
		this.name = name;
	}

	/**
	 * Gets the code
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Gets the name
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the regimen definitions
	 * @return the regimen definitions
	 */
	public List<RegimenDefinition> getRegimens() {
		return regimens;
	}

	/**
	 * Adds a regimen definition
	 * @param regimen the regimen definition
	 */
	public void addRegimen(RegimenDefinition regimen) {
		regimens.add(regimen);
	}
}