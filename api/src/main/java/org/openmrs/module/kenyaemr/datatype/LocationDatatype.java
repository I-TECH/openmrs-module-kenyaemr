/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.datatype;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.SerializingCustomDatatype;
import org.springframework.stereotype.Component;

/**
 * Custom datatype for {@link org.openmrs.Location}.
 * (This should be moved to the OpenMRS core.)  
 */
@Component("kenyaemrLocationDatatype")
public class LocationDatatype extends SerializingCustomDatatype<Location> {
	
	/**
	 * @see org.openmrs.customdatatype.SerializingCustomDatatype#deserialize(java.lang.String)
	 */
	@Override
	public Location deserialize(String serializedValue) {
		if (StringUtils.isEmpty(serializedValue)) {
			return null;
		}

		return Context.getLocationService().getLocation(Integer.valueOf(serializedValue));
	}
	
	/**
	 * @see org.openmrs.customdatatype.SerializingCustomDatatype#serialize(java.lang.Object)
	 */
	@Override
	public String serialize(Location typedValue) {
		if (typedValue == null) {
			return null;
		}

		return typedValue.getLocationId().toString();
	}
}