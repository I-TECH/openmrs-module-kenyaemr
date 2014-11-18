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

package org.openmrs.module.kenyaemr.system;

import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.requirement.Requirement;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Requirement for CIEL dictionary. This is instantiated as a bean in the application context so that the version value
 * can be taken from the project POM.
 */
public class CielRequirement implements Requirement {

	/**
	 * Name of global property that stores database concepts version
	 */
	protected static final String GP_CONCEPTS_VERSION = "ciel.conceptsVersion";

	public String requiredVersion;

	/**
	 * @see org.openmrs.module.kenyacore.requirement.Requirement#getName()
	 */
	@Override
	public String getName() {
		return "CIEL Dictionary";
	}

	/**
	 * @see org.openmrs.module.kenyacore.requirement.Requirement#getRequiredVersion()
	 */
	@Override
	public String getRequiredVersion() {
		return requiredVersion;
	}

	/**
	 * Sets the required version
	 * @param requiredVersion the required version
	 */
	public void setRequiredVersion(String requiredVersion) {
		this.requiredVersion = requiredVersion;
	}

	/**
	 * @see org.openmrs.module.kenyacore.requirement.Requirement#getFoundVersion()
	 */
	@Override
	public String getFoundVersion() {
		return Context.getAdministrationService().getGlobalProperty(GP_CONCEPTS_VERSION);
	}

	/**
	 * @see org.openmrs.module.kenyacore.requirement.Requirement#isSatisfied()
	 */
	@Override
	public boolean isSatisfied() {
		return checkCielVersions(requiredVersion, getFoundVersion());
	}

	/**
	 * Checks found CIEL version against the required version.
	 * @param required the required version
	 * @param found the found version
	 * @return true if found version is equal or greater to the required version
	 */
	protected static boolean checkCielVersions(String required, String found) {
		if (found == null) {
			return false;
		}

		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		try {
			Date requiredDate = format.parse(required);
			Date foundDate = format.parse(found);

			return foundDate.equals(requiredDate) || foundDate.after(requiredDate);
		} catch (Exception e) {
			return false;
		}
	}
}
