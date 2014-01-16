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
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.wrapper.Facility;
import org.openmrs.module.metadatadeploy.sync.ObjectSynchronization;
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
	 * @see org.openmrs.module.metadatadeploy.sync.ObjectSynchronization#fetchExistingById(int)
	 */
	@Override
	public Location fetchExistingById(int id) {
		return locationService.getLocation(id);
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.sync.ObjectSynchronization#getObjectSyncKey(org.openmrs.OpenmrsObject)
	 */
	@Override
	public Object getObjectSyncKey(Location obj) {
		return new Facility(obj).getMflCode();
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.sync.ObjectSynchronization#getObjectHash(org.openmrs.OpenmrsObject)
	 */
	@Override
	public String getObjectHash(Location obj) {
		Facility facility = new Facility(obj);

		return EmrUtils.hash(
				obj.getName(),
				obj.getDescription(),

				facility.getProvince(),
				facility.getCounty(),
				facility.getDistrict(),
				facility.getDivision(),
				facility.getTelephoneLandline(),
				facility.getTelephoneMobile(),
				facility.getPostCode()
		);
	}
}