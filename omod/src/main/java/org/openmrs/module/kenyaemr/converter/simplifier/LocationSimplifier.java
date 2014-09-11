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

package org.openmrs.module.kenyaemr.converter.simplifier;

import org.openmrs.Location;
import org.openmrs.module.kenyaemr.wrapper.Facility;
import org.openmrs.module.kenyaui.simplifier.AbstractMetadataSimplifier;
import org.openmrs.ui.framework.SimpleObject;
import org.springframework.stereotype.Component;

/**
 * Converts a location to a simple object
 */
@Component
public class LocationSimplifier extends AbstractMetadataSimplifier<Location> {

	/**
	 * @see org.openmrs.module.kenyaui.simplifier.AbstractMetadataSimplifier#simplify(Object)
	 */
	@Override
	protected SimpleObject simplify(Location location) {
		SimpleObject ret = super.simplify(location);

		Facility facility = new Facility(location);
		ret.put("code", facility.getMflCode());
		ret.put("country", facility.getCountry());
		ret.put("province", facility.getProvince());
		ret.put("county", facility.getCounty());
		ret.put("district", facility.getDistrict());
		ret.put("division", facility.getDivision());
		ret.put("postcode", facility.getPostCode());
		ret.put("landline", facility.getTelephoneLandline());
		ret.put("mobile", facility.getTelephoneMobile());
		ret.put("fax", facility.getTelephoneFax());
		return ret;
	}
}