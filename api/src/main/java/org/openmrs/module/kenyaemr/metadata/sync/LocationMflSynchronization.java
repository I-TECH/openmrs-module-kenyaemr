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

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.Location;

import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaemr.wrapper.Facility;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.source.ObjectSource;

import java.util.List;

/**
 * Synchronization operation to sync locations with a CSV copy of the Kenya Master Facility List
 */
public class LocationMflSynchronization extends AbstractSynchronization<Location> {

	/**
	 * Creates a new MFL CSV synchronization operation
	 * @param source the location source
	 */
	public LocationMflSynchronization(ObjectSource<Location> source) {
		super(source);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.metadata.sync.AbstractSynchronization#fetchAllExisting()
	 */
	@Override
	public List<Location> fetchAllExisting() {
		return Context.getLocationService().getAllLocations(true);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.metadata.sync.AbstractSynchronization#fetchExistingById(int)
	 */
	@Override
	public Location fetchExistingById(int id) {
		return Context.getLocationService().getLocation(id);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.metadata.sync.AbstractSynchronization#getObjectSyncKey(org.openmrs.OpenmrsMetadata)
	 */
	@Override
	public Object getObjectSyncKey(Location obj) {
		return new Facility(obj).getMflCode();
	}

	/**
	 * @see org.openmrs.module.kenyaemr.metadata.sync.AbstractSynchronization#getObjectHash(org.openmrs.OpenmrsMetadata)
	 */
	@Override
	public String getObjectHash(Location obj) {
		return EmrUtils.hash(
				obj.getName(),
				obj.getDescription(),

				obj.getAddress5(),
				obj.getAddress6(),
				obj.getCountyDistrict(),
				obj.getStateProvince(),
				obj.getCountry(),
				obj.getPostalCode()
		);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.metadata.sync.AbstractSynchronization#mergeWithExisting(org.openmrs.OpenmrsMetadata, org.openmrs.OpenmrsMetadata)
	 */
	@Override
	protected void mergeWithExisting(Location existing, Location incoming) {
		// TODO this is slow when you're saving a thousand locations
		LocationAttributeType codeAttrType = MetadataUtils.getLocationAttributeType(CommonMetadata._LocationAttributeType.MASTER_FACILITY_CODE);

		LocationAttribute existingCodeAttr = existing.getActiveAttributes(codeAttrType).get(0);
		LocationAttribute incomingCodeAttr = incoming.getActiveAttributes(codeAttrType).get(0);

		incomingCodeAttr.setId(existingCodeAttr.getId());
		Context.evictFromSession(existingCodeAttr);

		incoming.setId(existing.getId());
		Context.evictFromSession(existing);
	}
}