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

	/**
	 * Setup the Child Welfare Clinic number identifier source
	 * @param startFrom the base identifier to start from
	 */
	void setupCWCNumberIdentifierSource(String startFrom);
	
	/**
	 * Generates the next CWC number identifier value
	 * @param comment the reference comment
	 * @return the identifier value
	 */
	String getNextCWCNumber(String comment);
}