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

package org.openmrs.module.kenyaemr.util;

import java.util.Date;

/**
 * Bean class to hold build properties from Maven
 */
public class BuildProperties {

	private String version;

	private Date buildDate;

	private String developer;

	/**
	 * Gets the build version
	 * @return the build version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the build version
	 * @param version the build version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Gets the build date
	 * @return the build date
	 */
	public Date getBuildDate() {
		return buildDate;
	}

	/**
	 * Sets the build date
	 * @param buildDate the build date
	 */
	public void setBuildDate(Date buildDate) {
		this.buildDate = buildDate;
	}

	/**
	 * Gets the developer name
	 * @return the developer name
	 */
	public String getDeveloper() {
		return developer;
	}

	/**
	 * Sets the developer name
	 * @param developer the developer name
	 */
	public void setDeveloper(String developer) {
		this.developer = developer;
	}
}