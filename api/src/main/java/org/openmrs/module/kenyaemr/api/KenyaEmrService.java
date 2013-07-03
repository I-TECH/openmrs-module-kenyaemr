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
package org.openmrs.module.kenyaemr.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.LocationAttributeType;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic methods for KenyaEMR
 */
@Transactional
public interface KenyaEmrService extends OpenmrsService {
	
	/**
	 * Get if this server has been properly configured
	 * @return whether or not all required settings in the application are configured.
	 * @should return false before default location has been set
	 * @should return true after everything is configured
	 */
	@Transactional(readOnly = true)
	boolean isConfigured();
	
	/**
	 * Sets the default location for this server, i.e. the value that should be auto-set for new
	 * encounters, visits, etc.
	 * @param location the location
	 */
	void setDefaultLocation(Location location);

	/**
	 * Gets the default location for this server.
	 * @return the default location
	 * @should get the default location when set
	 */
	@Transactional(readOnly = true)
	Location getDefaultLocation();

	/**
	 * Gets the Master Facility List code for the default location for this server
	 * @return the Master Facility List code
	 */
	@Transactional(readOnly = true)
	String getDefaultLocationMflCode();

	/**
	 * Gets the location with the given Master Facility List code
	 * @return the location (null if no location has the given code)
	 * @should find the location with that code
	 * @should return null if no location has that code
	 */
	@Transactional(readOnly = true)
	Location getLocationByMflCode(String mflCode);

	/**
	 * Gets the visits that occurred for the given patient on the given date
	 * @param patient the patient
	 * @param date the day
	 * @return the visits
	 */
	@Transactional(readOnly = true)
	List<Visit> getVisitsByPatientAndDay(Patient patient, Date date);

	/**
	 * Gets the locations matching the specified arguments
	 *
	 * NEEDS MOVED INTO LocationService
	 *
	 * @param nameFragment is the string used to search for locations
	 * @param parent only return children of this parent
	 * @param attributeValues the attribute values
	 * @param includeRetired specifies if retired locations should also be returned
	 * @param start the beginning index
	 * @param length the number of matching locations to return
	 * @return the list of locations
	 */
	@Transactional(readOnly = true)
	@Authorized( { PrivilegeConstants.VIEW_LOCATIONS })
	List<Location> getLocations(String nameFragment, Location parent, Map<LocationAttributeType, Object> attributeValues, boolean includeRetired, Integer start, Integer length);
}