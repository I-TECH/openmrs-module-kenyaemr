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

import org.openmrs.module.kenyacore.AbstractContentConfiguration;

/**
 * Configuration bean class for regimens
 */
public class RegimenConfiguration extends AbstractContentConfiguration {

	private String definitionsPath;

	/**
	 * Gets the path to the definitions XML
	 * @return the path
	 */
	public String getDefinitionsPath() {
		return definitionsPath;
	}

	/**
	 * Sets the path to the definitions XML
	 * @param definitionsPath the path
	 */
	public void setDefinitionsPath(String definitionsPath) {
		this.definitionsPath = definitionsPath;
	}
}