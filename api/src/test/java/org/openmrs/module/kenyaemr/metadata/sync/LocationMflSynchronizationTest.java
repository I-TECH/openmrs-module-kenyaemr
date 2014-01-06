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
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.source.ObjectSource;
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

	@Before
	public void setup() {
		commonMetadata.install();
	}

	@Test
	public void integration() throws Exception {
		LocationAttributeType codeAttrType = MetadataUtils.getLocationAttributeType(CommonMetadata._LocationAttributeType.MASTER_FACILITY_CODE);

		// First sync should create 3 new locations
		ObjectSource<Location> source1 = new LocationMflCsvSource("test-locations.csv", codeAttrType);
		AbstractSynchronization synchronization1 = new LocationMflSynchronization(source1, codeAttrType);
		synchronization1.run();

		Assert.assertThat(synchronization1.getCreatedObjects(), hasSize(3));
		Assert.assertThat(synchronization1.getUpdatedObjects(), hasSize(0));
		Assert.assertThat(synchronization1.getRetiredObjects(), hasSize(0));

		//printAllLocations();

		// Modify a location's name
		Location modified = Context.getLocationService().getLocation("Abdisamad Dispensary");
		modified.setName("Modified");
		Context.getLocationService().saveLocation(modified);

		// Second sync should update that location
		ObjectSource<Location> source2 = new LocationMflCsvSource("test-locations.csv", codeAttrType);
		AbstractSynchronization synchronization2 = new LocationMflSynchronization(source2, codeAttrType);
		synchronization2.run();

		Assert.assertThat(Context.getLocationService().getLocation("Abdisamad Dispensary"), notNullValue());
		Assert.assertThat(synchronization2.getCreatedObjects(), hasSize(0));
		Assert.assertThat(synchronization2.getUpdatedObjects(), hasSize(1));
		Assert.assertThat(synchronization2.getRetiredObjects(), hasSize(0));

		//printAllLocations();

		// Modify a location's MFL code (effectively invalidating it)
		Location invalid = Context.getLocationService().getLocation("Abdisamad Dispensary");
		invalid.getActiveAttributes(codeAttrType).get(0).setValue("66666");
		Context.getLocationService().saveLocation(invalid);

		// Third sync should retire Abdisamad Dispensar (66666) and re-create Abdisamad Dispensary (17009)
		ObjectSource<Location> source3 = new LocationMflCsvSource("test-locations.csv", codeAttrType);
		AbstractSynchronization synchronization3 = new LocationMflSynchronization(source3, codeAttrType);
		synchronization3.run();

		Assert.assertThat(synchronization3.getCreatedObjects(), hasSize(1));
		Assert.assertThat(synchronization3.getUpdatedObjects(), hasSize(0));
		Assert.assertThat(synchronization3.getRetiredObjects(), hasSize(1));

		//printAllLocations();

		// Fourth sync should change nothing
		ObjectSource<Location> source4 = new LocationMflCsvSource("test-locations.csv", codeAttrType);
		AbstractSynchronization synchronization4 = new LocationMflSynchronization(source4, codeAttrType);
		synchronization4.run();

		Assert.assertThat(synchronization4.getCreatedObjects(), hasSize(0));
		Assert.assertThat(synchronization4.getUpdatedObjects(), hasSize(0));
		Assert.assertThat(synchronization4.getRetiredObjects(), hasSize(0));

		//printAllLocations();
	}

	private void printAllLocations() {
		LocationAttributeType codeAttrType = MetadataUtils.getLocationAttributeType(CommonMetadata._LocationAttributeType.MASTER_FACILITY_CODE);

		for (Location loc : Context.getLocationService().getAllLocations()) {
			List<LocationAttribute>  attrs = loc.getActiveAttributes(codeAttrType);
			String code = attrs.size() > 0 ? (String) attrs.get(0).getValue() : "?";
			System.out.println(loc.getId() + " | " + code + " | " + loc.getName() + " | " + loc.getUuid() + " | " + (loc.isRetired() ? "retired" : ""));
		}
		System.out.println();
	}
}