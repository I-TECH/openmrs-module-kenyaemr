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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests for {@link LocationMflSynchronization}
 */
@Ignore
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
		LocationAttributeType codeAttrType = MetadataUtils.existing(LocationAttributeType.class, FacilityMetadata._LocationAttributeType.MASTER_FACILITY_CODE);

		// First sync should create 3 new locations
		SyncResult<Location> result = runSync();

		Assert.assertThat(result.getCreated(), hasSize(3));
		Assert.assertThat(result.getUpdated(), hasSize(0));
		Assert.assertThat(result.getRetired(), hasSize(0));

		// Second sync should do nothing
		result = runSync();

		Assert.assertThat(result.getCreated(), hasSize(0));
		Assert.assertThat(result.getUpdated(), hasSize(0));
		Assert.assertThat(result.getRetired(), hasSize(0));

		// Modify a location's name
		Location modified = locationService.getLocation("Abdisamad Dispensary");
		modified.setName("Modified");
		locationService.saveLocation(modified);

		printAllLocations();

		// Third sync should reset the name of that location
		result = runSync();

		Assert.assertThat(locationService.getLocation("Abdisamad Dispensary"), notNullValue());
		Assert.assertThat(result.getCreated(), hasSize(0));
		Assert.assertThat(result.getUpdated(), hasSize(1));
		Assert.assertThat(result.getRetired(), hasSize(0));

		// Modify a location's MFL code (effectively invalidating it)
		Location invalid = locationService.getLocation("Abdisamad Dispensary");
		invalid.getActiveAttributes(codeAttrType).get(0).setValue("66666");
		locationService.saveLocation(invalid);

		// Fourth sync should retire Abdisamad Dispensar (66666) and re-create Abdisamad Dispensary (17009)
		result = runSync();

		Assert.assertThat(result.getCreated(), hasSize(0));
		Assert.assertThat(result.getUpdated(), hasSize(0));
		Assert.assertThat(result.getRetired(), hasSize(3));

		// Final sync should change nothing
		result = runSync();

		Assert.assertThat(result.getCreated(), hasSize(0));
		Assert.assertThat(result.getUpdated(), hasSize(0));
		Assert.assertThat(result.getRetired(), hasSize(0));

		Context.flushSession();

		// Check locations have only one code attribute max
		for (Location loc : locationService.getAllLocations()) {
			List<LocationAttribute> codeAttrs = loc.getActiveAttributes(codeAttrType);
			Assert.assertThat(codeAttrs.size(), lessThanOrEqualTo(1));
		}
	}

	private SyncResult<Location> runSync() throws Exception {
		ObjectSource<Location> source = new LocationMflCsvSource("test-locations.csv");
		SyncResult<Location> result = new MetadataSynchronizationRunner<Location>(source, mflSynchronization).run();

		printAllLocations();

		return result;
	}

	private void printAllLocations() {
		for (Location loc : locationService.getAllLocations()) {
			Facility facility = new Facility(loc);
			System.out.println(loc.getId() + " | " + facility.getMflCode() + " | " + loc.getName() + " | " + loc.getUuid() + " | " + (loc.isRetired() ? "retired" : ""));
		}
		System.out.println();
	}
}