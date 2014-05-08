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