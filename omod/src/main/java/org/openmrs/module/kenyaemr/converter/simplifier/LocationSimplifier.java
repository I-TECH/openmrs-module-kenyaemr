/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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