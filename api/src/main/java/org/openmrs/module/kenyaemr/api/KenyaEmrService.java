/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.OpenmrsService;
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
	boolean isSetupRequired();
	
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
	 * Generates the next unique patient number identifier value
	 * @param comment the reference comment
	 * @return the identifier value
	 */
	String getNextHivUniquePatientNumber(String comment);

	/**
	 * Gets the visits that occurred for the given patient on the given date
	 * @param patient the patient
	 * @param date the day
	 * @return the visits
	 */
	@Transactional(readOnly = true)
	List<Visit> getVisitsByPatientAndDay(Patient patient, Date date);

	/**
	 * Setup the medical record number identifier source
	 * @param startFrom the base identifier to start from
	 */
	void setupMrnIdentifierSource(String startFrom);

	/**
	 * Setup the unique patient number identifier source
	 * @param startFrom the base identifier to start from
	 */
	void setupHivUniqueIdentifierSource(String startFrom);

	public List<Object> executeSqlQuery(String query, Map<String, Object> substitutions);
	public List<Object> executeHqlQuery(String query, Map<String, Object> substitutions);
}