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

import java.util.List;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.LocationAttributeType;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.kenyaemr.report.ReportManager;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business methods for the Kenya EMR application
 */
public interface KenyaEmrService extends OpenmrsService {
	
	/**
	 * Get if this server has been properly configured
	 * @return whether or not all required settings in the application are configured.
	 * @should return false before default location has been set
	 * @should return true after everything is configured
	 */
	boolean isConfigured();
	
	/**
	 * Sets the default location for this server, i.e. the value that should be auto-set for new
	 * encounters, visits, etc.
	 * @param location the location
	 */
	@Transactional
	void setDefaultLocation(Location location);

	/**
	 * Gets the default location for this server.
	 * @return the default location
	 * @throws ConfigurationRequiredException if default location is not configured
	 * @should throw an exception if the default location has not been set
	 * @should get the default location when set
	 */
	@Transactional(readOnly=true)
	Location getDefaultLocation();

	/**
	 * Gets the Master Facility List code for the default location for this server
	 * @return the Master Facility List code
	 * @throws ConfigurationRequiredException if default location is not configured
	 */
	String getDefaultLocationMflCode();

	/**
	 * Gets the location with the given Master Facility List code
	 * @return the location (null if no location has the given code)
	 * @should find the location with that code
	 * @should return null if no location has that code
	 */
	Location getLocationByMflCode(String mflCode);
	
	/**
	 * Sets up a new idgen identifier source for our auto-generated medical record numbers
	 * 
	 * @param startFrom the first identifier to use
	 * 
	 * @should set up an identifier source
	 * @should fail if already set up
	 */
	@Transactional
	void setupMrnIdentifierSource(String startFrom);
	
	/**
	 * Sets up a new idgen identifier source for our auto-generated HIV Unique Patient Numbers
	 * 
	 * @param startFrom the first identifier to use
	 * 
	 * @should set up an identifier source
	 * @should fail if already set up
	 */
	@Transactional
	void setupHivUniqueIdentifierSource(String startFrom);

	/**
     * @return the ID generator for Medical Record Numbers (OpenMRS IDs)
     * @throws ConfigurationRequiredException if the ID source has not be set up yet
     */
    IdentifierSource getMrnIdentifierSource() throws ConfigurationRequiredException;
    
    /**
     * @return the ID generator for HIV Unique Patient Numbers
     * @throws ConfigurationRequiredException if the ID source has not be set up yet
     */
    IdentifierSource getHivUniqueIdentifierSource() throws ConfigurationRequiredException;

	/**
     * Called at spring context refresh to refresh the list of known report managers 
     */
    void refreshReportManagers();

    /**
     * @param className
     * @return the ReportDefinition that was setup by the {@link ReportManager} with the given classname
     */
    ReportManager getReportManager(String className);

	/**
	 * @param withTag null means get all report managers
     * @return all registered report managers with the given tag
     */
    List<ReportManager> getReportManagersByTag(String withTag);
    
    /**
     * @return the next HIV Unique Patient Number, including both the MFL prefix, and an idgen-generated sequential number
     * @should get sequential numbers with mfl prefix
     */
    String getNextHivUniquePatientNumber(String comment);

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