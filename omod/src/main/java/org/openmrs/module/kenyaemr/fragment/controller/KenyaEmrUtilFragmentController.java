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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.ui.framework.SimpleObject;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Fragment actions generally useful for the Kenya EMR module
 */
public class KenyaEmrUtilFragmentController {

	// no controller method, only actions
	
	public List<SimpleObject> locationSearch(@RequestParam(required=false, value="term") String query) {
		LocationService ls = Context.getLocationService();
		
		List<Location> results = new ArrayList<Location>();
		// always show default location and its sub-locations
		Location defaultLocation = Context.getService(KenyaEmrService.class).getDefaultLocation();
		results.add(defaultLocation);
		results.addAll(defaultLocation.getChildLocations(false));
		
		if (StringUtils.isNotBlank(query)) {
			results.addAll(ls.getLocations(query, true, 0, 50));
		}
		
		// TODO move this to MetadataConstants
		LocationAttributeType facilityCode = ls.getLocationAttributeType(1);
		
		List<SimpleObject> ret = new ArrayList<SimpleObject>();
		for (Location l : results) {
			List<LocationAttribute> attrs = l.getActiveAttributes(facilityCode);
			String display = attrs.size() > 0 ? (l.getName() + " (" + attrs.get(0).getValue() + ")") : (" -> " + l.getName());
			ret.add(SimpleObject.create("value", l.getLocationId(), "label", display));
		}
		return ret;
	}

}
