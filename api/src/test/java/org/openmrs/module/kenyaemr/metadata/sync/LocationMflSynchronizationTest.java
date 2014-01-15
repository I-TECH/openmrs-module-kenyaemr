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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.FacilityMetadata;
import org.openmrs.module.kenyaemr.wrapper.Facility;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.source.ObjectSource;
import org.openmrs.module.metadatadeploy.sync.MetadataSynchronizationRunner;
import org.openmrs.module.metadatadeploy.sync.SyncResult;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link LocationMflSynchronization}
 */
public class LocationMflSynchronizationTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private FacilityMetadata facilityMetadata;

	@Autowired
	private LocationService locationService;

	@Autowired
	private LocationMflSynchronization mflSynchronization;

	@Before
	public void setup() throws Exception {
		commonMetadata.install();
		facilityMetadata.install(false); // Don't do full facility sync
	}

	@Test
	public void integration() throws Exception {
		LocationAttributeType codeAttrType = MetadataUtils.getLocationAttributeType(FacilityMetadata._LocationAttributeType.MASTER_FACILITY_CODE);

		// First sync should create 3 new locations
		ObjectSource<Location> source1 = new LocationMflCsvSource("test-locations.csv", codeAttrType);
		SyncResult<Location> result1 = new MetadataSynchronizationRunner<Location>(source1, mflSynchronization).run();

		Assert.assertThat(result1.getCreated(), hasSize(3));
		Assert.assertThat(result1.getUpdated(), hasSize(0));
		Assert.assertThat(result1.getRetired(), hasSize(0));

		//printAllLocations();

		// Modify a location's name
		Location modified = locationService.getLocation("Abdisamad Dispensary");
		modified.setName("Modified");
		locationService.saveLocation(modified);

		// Second sync should update that location
		ObjectSource<Location> source2 = new LocationMflCsvSource("test-locations.csv", codeAttrType);
		SyncResult<Location> result2 = new MetadataSynchronizationRunner<Location>(source2, mflSynchronization).run();

		Assert.assertThat(locationService.getLocation("Abdisamad Dispensary"), notNullValue());
		Assert.assertThat(result2.getCreated(), hasSize(0));
		Assert.assertThat(result2.getUpdated(), hasSize(1));
		Assert.assertThat(result2.getRetired(), hasSize(0));

		//printAllLocations();

		// Modify a location's MFL code (effectively invalidating it)
		Location invalid = locationService.getLocation("Abdisamad Dispensary");
		invalid.getActiveAttributes(codeAttrType).get(0).setValue("66666");
		locationService.saveLocation(invalid);

		// Third sync should retire Abdisamad Dispensar (66666) and re-create Abdisamad Dispensary (17009)
		ObjectSource<Location> source3 = new LocationMflCsvSource("test-locations.csv", codeAttrType);
		SyncResult<Location> result3 = new MetadataSynchronizationRunner<Location>(source3, mflSynchronization).run();

		Assert.assertThat(result3.getCreated(), hasSize(1));
		Assert.assertThat(result3.getUpdated(), hasSize(0));
		Assert.assertThat(result3.getRetired(), hasSize(1));

		//printAllLocations();

		// Fourth sync should change nothing
		ObjectSource<Location> source4 = new LocationMflCsvSource("test-locations.csv", codeAttrType);
		SyncResult<Location> result4 = new MetadataSynchronizationRunner<Location>(source4, mflSynchronization).run();

		Assert.assertThat(result4.getCreated(), hasSize(0));
		Assert.assertThat(result4.getUpdated(), hasSize(0));
		Assert.assertThat(result4.getRetired(), hasSize(0));

		//printAllLocations();

		Context.flushSession();

		// Check locations have only one code attribute max
		for (Location loc : locationService.getAllLocations()) {
			List<LocationAttribute> codeAttrs = loc.getActiveAttributes(codeAttrType);
			Assert.assertThat(codeAttrs.size(), lessThanOrEqualTo(1));
		}
	}

	private void printAllLocations() {
		for (Location loc : locationService.getAllLocations()) {
			Facility facility = new Facility(loc);
			System.out.println(loc.getId() + " | " + facility.getMflCode() + " | " + loc.getName() + " | " + loc.getUuid() + " | " + (loc.isRetired() ? "retired" : ""));
		}
		System.out.println();
	}
}