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

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.context.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Synchronization of facilities from a CSV version of the Kenyan Master Facility List
 *
 * This could become a generic CSV location import utility class in the metadatadeploy module
 */
public class FacilityListSynchronization {

	protected static final Log log = LogFactory.getLog(FacilityListSynchronization.class);

	protected LocationAttributeType codeAttrType;

	protected Map<String, Integer> mflCodeCache = new HashMap<String, Integer>();

	protected Set<Integer> notSyncedLocations = new HashSet<Integer>();;

	protected int createdCount = 0;

	protected int updatedCount = 0;

	protected int retiredCount = 0;

	/**
	 * Creates and performs a synchronization of facilities from the given CSV list
	 * @param csvFile the CSV facility list
	 */
	public FacilityListSynchronization(String csvFile, LocationAttributeType codeAttrType) {
		this.codeAttrType = codeAttrType;

		preImport();

		log.info("Loaded " + mflCodeCache.size() + " existing locations with MFL codes");

		InputStream in = FacilityListSynchronization.class.getClassLoader().getResourceAsStream(csvFile);
		CSVReader reader = new CSVReader(new InputStreamReader(in));

		String[] nextLine;
		try {
			while ((nextLine = reader.readNext()) != null) {
				String code = nextLine[0];
				String name = nextLine[1];
				String province = nextLine[2];
				String type = nextLine[6];

				if (StringUtils.isEmpty(name)) {
					log.error("Unable to import location " + code + " with empty name");
				} else if (StringUtils.isEmpty(code)) {
					log.error("Unable to import location '" + name + "' with invalid code");
				} else {
					importLocation(code.trim(), name.trim(), province, type);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		postImport();
	}

	/**
	 * Pre-import logic - prepares caches etc
	 */
	protected void preImport() {
		// Examine existing locations
		for (Location loc : Context.getLocationService().getAllLocations(true)) {
			List<LocationAttribute> mfcAttrs = loc.getActiveAttributes(codeAttrType);

			if (mfcAttrs.size() == 0) {
				log.warn("Ignoring location '" + loc.getName() + "' with no MFL code");
			}
			else if (mfcAttrs.size() > 1) {
				log.warn("Ignoring location '" + loc.getName() + "' with multiple MFL codes");
			}
			else {
				String mflCode = (String) mfcAttrs.get(0).getValue();

				// Check there isn't another location with this code
				if (lookupMflCodeCache(mflCode) != null) {
					log.warn("Ignoring location '" + loc.getName() + "' with duplicate MFL code " + mflCode);
				}
				else {
					updateMflCodeCache(mflCode, loc);
					notSyncedLocations.add(loc.getLocationId());
				}
			}
		}
	}

	/**
	 * Post-import logic
	 */
	protected void postImport() {
		// Retire locations that weren't in the MFL
		for (Integer notSyncedLocId : notSyncedLocations) {
			Location notSyncedLoc = Context.getLocationService().getLocation(notSyncedLocId);
			if (!notSyncedLoc.getRetired()) {
				notSyncedLoc.setRetired(true);
				notSyncedLoc.setRetiredBy(Context.getAuthenticatedUser());
				notSyncedLoc.setRetireReason("No longer in MFL");
				Context.getLocationService().saveLocation(notSyncedLoc);

				log.info("Retired existing location '" + notSyncedLoc.getName() + "'");
				retiredCount++;
			}
		}
	}

	/**
	 * Imports a location
	 * @param code the MFL code
	 * @param name the location name
	 * @param province the province
	 * @param type location type
	 */
	protected void importLocation(String code, String name, String province, String type) {
		// Map MFL fields to location properties
		String locationName = name;
		String locationDescription = type;
		String locationStateProvince = province;
		String locationCountry = "Kenya";

		// Look for existing location with this code
		Location location = lookupMflCodeCache(code);

		boolean doCreate = false, doUpdate = false;

		// Create new location if it doesn't exist
		if (location == null) {
			location = new Location();
			location.setCreator(Context.getAuthenticatedUser());
			location.setDateCreated(new Date());

			// Create MFL code attribute for new location
			LocationAttribute mfcAttr = new LocationAttribute();
			mfcAttr.setAttributeType(codeAttrType);
			mfcAttr.setValue(code);
			mfcAttr.setOwner(location);
			location.addAttribute(mfcAttr);

			doCreate = true;
		}
		else {
			// Un-retire location if necessary
			if (location.getRetired()) {
				location.setRetired(false);
				location.setRetiredBy(null);
				location.setRetireReason(null);
				doUpdate = true;
			}
			else {
				// Compute hashes of existing location fields and incoming fields
				String incomingHash = EmrUtils.hash(locationName, locationDescription, locationStateProvince, locationCountry);
				String existingHash = EmrUtils.hash(location.getName(), location.getDescription(), location.getStateProvince(), location.getCountry());

				// Only update if hashes are different
				if (!incomingHash.equals(existingHash)) {
					doUpdate = true;
				}
			}
		}

		if (doCreate) {
			log.info("Creating new location '" + locationName + "' with code " + code);
			createdCount++;
		}
		else if (doUpdate) {
			log.info("Updating existing location '" + locationName + "' with code " + code);
			updatedCount++;
		}

		if (doCreate || doUpdate) {
			location.setName(locationName);
			location.setDescription(locationDescription);
			location.setStateProvince(locationStateProvince);
			location.setCountry(locationCountry);

			Context.getLocationService().saveLocation(location);
			updateMflCodeCache(code, location);
		}

		markLocationSynced(location);
	}

	/**
	 * Updates the cache of MFL codes
	 * @param code the code
	 * @param location the location
	 */
	protected void updateMflCodeCache(String code, Location location) {
		mflCodeCache.put(code, location.getLocationId());
	}

	/**
	 * Lookup a location in the cache of MFL codes
	 * @param code the code
	 * @return the location
	 */
	protected Location lookupMflCodeCache(String code) {
		Integer locationId = mflCodeCache.get(code);
		return locationId != null ? Context.getLocationService().getLocation(locationId) : null;
	}

	/**
	 * Marks a location as synced
	 * @param location the location
	 */
	protected void markLocationSynced(Location location) {
		notSyncedLocations.remove(location.getLocationId());
	}

	/**
	 * Gets the count of created facilities
	 * @return the count
	 */
	public int getCreatedCount() {
		return createdCount;
	}

	/**
	 * Gets the count of updated facilities
	 * @return the count
	 */
	public int getUpdatedCount() {
		return updatedCount;
	}

	/**
	 * Gets the count of retired facilities
	 * @return the count
	 */
	public int getRetiredCount() {
		return retiredCount;
	}
}