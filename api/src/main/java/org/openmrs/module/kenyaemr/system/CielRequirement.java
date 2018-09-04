/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
