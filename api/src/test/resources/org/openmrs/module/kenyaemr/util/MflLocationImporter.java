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

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import liquibase.util.csv.CSVReader;

import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;


/**
 *
 */
public class MflLocationImporter {
	
	/**
	 * @param csv
	 * @return
	 * @throws IOException
	 * @should import csv content
	 */
	public int importCsv(String csv) throws IOException {
		CSVReader reader = new CSVReader(new StringReader(csv));
		return handleCsv(reader);
	}

    private int handleCsv(CSVReader reader) throws IOException {
    	LocationService ls = Context.getLocationService();
    	LocationAttributeType facilityCodeAttrType = ls.getLocationAttributeTypeByUuid(MetadataConstants.MASTER_FACILITY_CODE_LOCATION_ATTRIBUTE_TYPE_UUID);
    	
    	// can't look up a location by facility code (TRUNK-3589), so just fetch them all
    	Map<String, Location> byFacilityCode = new HashMap<String, Location>();
    	for (Location l : ls.getAllLocations()) {
    		for (LocationAttribute attr : l.getActiveAttributes(facilityCodeAttrType)) {
    			byFacilityCode.put((String) attr.getValue(), l);
    		}
    	}
    	
		String[] row;
		int saves = 0;
		while ((row = reader.readNext()) != null) {
	        String facilityCode = row[0].trim();
	        if (facilityCode.equals("Facility Code")) {
	        	// skip header row
	        	continue;
	        }
	        String facilityName = row[1].trim();
	        String county = row[2].trim();
	        String type = row[3].trim();
	        Location location = byFacilityCode.get(facilityCode);
	        if (location == null) {
	        	location = new Location();
	        	LocationAttribute attr = new LocationAttribute();
	        	attr.setAttributeType(facilityCodeAttrType);
	        	attr.setValue(facilityCode);
	        	location.addAttribute(attr);
	        }
	        location.setName(facilityName);
	        location.setDescription("(none)");
	        location.setCountyDistrict(county);
	        // skip type for now
	        ls.saveLocation(location);
	        saves += 1;
	    }
		return saves;
	}
	
}
