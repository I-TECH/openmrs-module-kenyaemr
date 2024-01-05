/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.wrapper;

import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.module.kenyacore.wrapper.AbstractCustomizableWrapper;
import org.openmrs.module.kenyaemr.metadata.FacilityMetadata;

/**
 * A facility wrapper for a location
 */
public class Facility extends AbstractCustomizableWrapper<Location, LocationAttribute> {

	/**
	 * Creates a new facility wrapper for a location
	 * @param location the location
	 */
	public Facility(Location location) {
		super(location);
	}

	/**
	 * Gets the master facility code
	 * @return the code
	 */
	public String getMflCode() {
		return (String) getAsAttribute(FacilityMetadata._LocationAttributeType.MASTER_FACILITY_CODE);
	}

	/**
	 * Gets the country
	 * @return the country
	 */
	public String getCountry() {
		return target.getCountry();
	}

	/**
	 * Gets the province
	 * @return the province
	 */
	public String getProvince() {
		return target.getStateProvince();
	}

	/**
	 * Gets the county
	 * @return the county
	 */
	public String getCounty() {
		return target.getCountyDistrict();
	}

	/**
	 * Gets the district
	 * @return the district
	 */
	public String getDistrict() {
		return target.getAddress6();
	}

	/**
	 * Gets the division
	 * @return the division
	 */
	public String getDivision() {
		return target.getAddress5();
	}

	/**
	 * Gets the post code
	 * @return the post code
	 */
	public String getPostCode() {
		return target.getPostalCode();
	}

	/**
	 * Gets the landline telephone number
	 * @return the number
	 */
	public String getTelephoneLandline() {
		return (String) getAsAttribute(FacilityMetadata._LocationAttributeType.TELEPHONE_LANDLINE);
	}

	/**
	 * Gets the mobile telephone number
	 * @return the number
	 */
	public String getTelephoneMobile() {
		return (String) getAsAttribute(FacilityMetadata._LocationAttributeType.TELEPHONE_MOBILE);
	}

	/**
	 * Gets the fax telephone number
	 * @return the number
	 */
	public String getTelephoneFax() {
		return (String) getAsAttribute(FacilityMetadata._LocationAttributeType.TELEPHONE_FAX);
	}
}