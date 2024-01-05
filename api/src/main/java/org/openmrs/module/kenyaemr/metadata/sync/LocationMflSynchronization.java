/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.metadata.sync;

import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.module.kenyaemr.wrapper.Facility;
import org.openmrs.module.metadatadeploy.sync.ObjectSynchronization;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Synchronization operation to sync locations with a CSV copy of the Kenya Master Facility List
 */
@Component
public class LocationMflSynchronization implements ObjectSynchronization<Location> {

	@Autowired
	private LocationService locationService;

	/**
	 * @see org.openmrs.module.metadatadeploy.sync.ObjectSynchronization#fetchAllExisting()
	 */
	@Override
	public List<Location> fetchAllExisting() {
		return locationService.getAllLocations(true);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.sync.ObjectSynchronization#getObjectSyncKey(org.openmrs.OpenmrsObject)
	 */
	@Override
	public Object getObjectSyncKey(Location obj) {
		return new Facility(obj).getMflCode();
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.sync.ObjectSynchronization#updateRequired(org.openmrs.OpenmrsObject, org.openmrs.OpenmrsObject)
	 */
	@Override
	public boolean updateRequired(Location incoming, Location existing) {
		Facility facility1 = new Facility(incoming);
		Facility facility2 = new Facility(existing);

		boolean objectsMatch = OpenmrsUtil.nullSafeEquals(incoming.getName(), existing.getName())
				&& OpenmrsUtil.nullSafeEquals(incoming.getDescription(), existing.getDescription())
				&& OpenmrsUtil.nullSafeEquals(facility1.getProvince(), facility2.getProvince())
				&& OpenmrsUtil.nullSafeEquals(facility1.getCounty(), facility2.getCounty())
				&& OpenmrsUtil.nullSafeEquals(facility1.getDistrict(), facility2.getDistrict())
				&& OpenmrsUtil.nullSafeEquals(facility1.getDivision(), facility2.getDivision())
				&& OpenmrsUtil.nullSafeEquals(facility1.getTelephoneLandline(), facility2.getTelephoneLandline())
				&& OpenmrsUtil.nullSafeEquals(facility1.getTelephoneFax(), facility2.getTelephoneFax())
				&& OpenmrsUtil.nullSafeEquals(facility1.getTelephoneMobile(), facility2.getTelephoneMobile())
				&& OpenmrsUtil.nullSafeEquals(facility1.getPostCode(), facility2.getPostCode());

		return !objectsMatch;
	}
}