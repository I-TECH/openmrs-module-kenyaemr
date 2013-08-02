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

package org.openmrs.module.kenyacore.metadata;

import org.openmrs.module.kenyacore.AbstractContentConfiguration;

import java.util.Map;

/**
 * Configuration bean class for metadata
 */
public class MetadataConfiguration extends AbstractContentConfiguration {

	private Map<String, String> packages;

	/**
	 * Gets the packages
	 * @return the packages
	 */
	public Map<String, String> getPackages() {
		return packages;
	}

	/**
	 * Sets the packages
	 * @param packages the packages
	 */
	public void setPackages(Map<String, String> packages) {
		this.packages = packages;
	}
}