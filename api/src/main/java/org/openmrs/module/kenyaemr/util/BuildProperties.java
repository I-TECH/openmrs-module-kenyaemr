/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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