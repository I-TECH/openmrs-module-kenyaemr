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

package org.openmrs.module.kenyaemr.metadata;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Locations metadata bundle which syncs locations against a CSV list of facilities
 */
@Component
@Requires({ CommonMetadata.class })
public class LocationsMetadata extends AbstractMetadataBundle {

	protected static final Log log = LogFactory.getLog(LocationsMetadata.class);

	public static final class _Location {
		public static final String UNKNOWN = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f";
	}

	private static final String CSV_RESOURCE = "facilities.csv";

	protected LocationAttributeType codeAttrType;

	protected Map<String, Integer> mflCodeCache;

	protected Set<Integer> notSyncedLocations;

	protected int createdCount = 0;

	protected int updatedCount = 0;

	protected int retiredCount = 0;

	private static MessageDigest md5Digest;

	static {
		try {
			// This is only for diffing values so MD5 is fine
			md5Digest = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException ex) {
			log.error(ex);
		}
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {
		preImport();

		log.info("Loaded " + mflCodeCache.size() + " existing locations with MFL codes");

		InputStream in = LocationsMetadata.class.getClassLoader().getResourceAsStream(CSV_RESOURCE);
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
				String incomingHash = hash(locationName, locationDescription, locationStateProvince, locationCountry);
				String existingHash = hash(location.getName(), location.getDescription(), location.getStateProvince(), location.getCountry());

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

		Context.flushSession();
		Context.clearSession();
	}

	protected void preImport() {
		mflCodeCache = new HashMap<String, Integer>();
		notSyncedLocations = new HashSet<Integer>();
		codeAttrType = MetadataUtils.getLocationAttributeType(CommonMetadata._LocationAttributeType.MASTER_FACILITY_CODE);

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
	 * Computes a hash of a set of values
	 * @param values the input values
	 * @return the hash value
	 */
	public static String hash(String... values) {
		StringBuilder sb = new StringBuilder();
		for (String value : values) {
			sb.append(value != null ? value : "xxxxxxxx");
		}

		md5Digest.update(sb.toString().getBytes());
		return Hex.encodeHexString(md5Digest.digest());
	}
}