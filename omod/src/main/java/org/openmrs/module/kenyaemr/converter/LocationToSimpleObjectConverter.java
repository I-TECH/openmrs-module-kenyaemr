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

package org.openmrs.module.kenyaemr.converter;

import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.module.kenyautil.MetadataUtils;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.ui.framework.SimpleObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Converts a location to a simple object
 */
@Component
public class LocationToSimpleObjectConverter implements Converter<Location, SimpleObject> {

	/**
	 * @see org.springframework.core.convert.converter.Converter#convert(Object)
	 */
	@Override
	public SimpleObject convert(Location location) {
		LocationAttributeType mflCodeAttrType = MetadataUtils.getLocationAttributeType(Metadata.MASTER_FACILITY_CODE_LOCATION_ATTRIBUTE_TYPE);
		List<LocationAttribute> attrs = location.getActiveAttributes(mflCodeAttrType);
		String facilityCode = attrs.size() > 0 ? (String)attrs.get(0).getValue() : null;

		SimpleObject ret = new SimpleObject();
		ret.put("id", location.getId());
		ret.put("name", location.getName());
		ret.put("code", facilityCode != null ? facilityCode : "?");
		return ret;
	}
}