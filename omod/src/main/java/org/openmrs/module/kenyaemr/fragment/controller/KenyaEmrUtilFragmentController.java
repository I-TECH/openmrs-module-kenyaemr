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
package org.openmrs.module.kenyaemr.fragment.controller;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.KenyaEmrUiUtils;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.api.ConfigurationRequiredException;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Fragment actions generally useful for the Kenya EMR module
 */
public class KenyaEmrUtilFragmentController {

	/**
	 * Searches for locations by name of MFL code
	 * @param term the search term
	 * @return the list of locations as simple objects
	 */
	public List<SimpleObject> locationSearch(@RequestParam(required = false, value = "term") String term, UiUtils ui) {
		LocationService svc = Context.getLocationService();
		LocationAttributeType mflCodeAttrType = svc.getLocationAttributeTypeByUuid(MetadataConstants.MASTER_FACILITY_CODE_LOCATION_ATTRIBUTE_TYPE_UUID);

		// Results will be sorted by name
		Set<Location> results = new TreeSet<Location>(new Comparator<Location>() {
			@Override
			public int compare(Location location1, Location location2) {
				return location1.getName().compareTo(location2.getName());
			}
		});

		// Add default location and its sub-locations
		try {
			Location defaultLocation = Context.getService(KenyaEmrService.class).getDefaultLocation();
			results.add(defaultLocation);
			results.addAll(defaultLocation.getChildLocations(false));
		} catch (ConfigurationRequiredException ex) {
			// pass
		}

		// If term looks like an MFL code, add location with that code
		if (StringUtils.isNumeric(term) && term.length() >= 5) {
			Location locationByMflCode = Context.getService(KenyaEmrService.class).getLocationByMflCode(term);
			if (locationByMflCode != null) {
				results.add(locationByMflCode);
			}
		}

		// Add first 20 results of search by name
		if (StringUtils.isNotBlank(term)) {
			results.addAll(svc.getLocations(term, true, 0, 20));
		}

		// Convert to simple objects
		List<SimpleObject> ret = new ArrayList<SimpleObject>();
		for (Location l : results) {
			ret.add(KenyaEmrUiUtils.simpleLocation(l, mflCodeAttrType, ui));
		}
		return ret;
	}

	/**
	 *
	 * @param comment
	 * @return
	 */
	public SimpleObject nextHivUniquePatientNumber(@RequestParam(required = false, value = "comment") String comment) {
		if (comment == null) {
			comment = "Kenya EMR UI";
		}
		String id = Context.getService(KenyaEmrService.class).getNextHivUniquePatientNumber(comment);
		return SimpleObject.create("value", id);
	}
}