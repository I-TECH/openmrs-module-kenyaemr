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