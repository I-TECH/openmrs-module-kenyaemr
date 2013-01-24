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
package org.openmrs.module.kenyaemr.api.db;

import org.openmrs.Location;
import org.openmrs.LocationAttributeType;

import java.util.List;
import java.util.Map;

/**
 * Database access functions
 */
public interface KenyaEmrDAO {

	/**
	 * Gets the locations matching the specified arguments
	 *
	 * NEEDS MOVED INTO LocationDAO
	 *
	 * @param nameFragment is the string used to search for locations
	 * @param parent only return children of this parent
	 * @param serializedAttributeValues the serialized attribute values
	 * @param includeRetired specifies if retired locations should also be returned
	 * @param start the beginning index
	 * @param length the number of matching locations to return
	 * @return the list of locations
	 */
 	List<Location> getLocations(String nameFragment, Location parent, Map<LocationAttributeType, String> serializedAttributeValues, boolean includeRetired, Integer start, Integer length);
}