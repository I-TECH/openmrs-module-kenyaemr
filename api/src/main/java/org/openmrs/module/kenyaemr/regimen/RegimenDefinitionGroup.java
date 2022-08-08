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