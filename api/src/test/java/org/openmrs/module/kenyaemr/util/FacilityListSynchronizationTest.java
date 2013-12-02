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

package org.openmrs.module.kenyaemr.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.LocationAttributeType;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link FacilityListSynchronization}
 */
public class FacilityListSynchronizationTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private KenyaEmrService kenyaEmrService;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() {
		commonMetadata.install();
	}

	/**
	 * @see FacilityListSynchronization#FacilityListSynchronization(String, org.openmrs.LocationAttributeType)
	 */
	@Test
	public void integration() {
		LocationAttributeType codeAttrType = MetadataUtils.getLocationAttributeType(CommonMetadata._LocationAttributeType.MASTER_FACILITY_CODE);

		FacilityListSynchronization sync = new FacilityListSynchronization("test-locations.csv", codeAttrType);
		Assert.assertThat(sync.getCreatedCount(), is(3));
		Assert.assertThat(sync.getUpdatedCount(), is(0));
		Assert.assertThat(sync.getRetiredCount(), is(0));

		// Check first location's details...
		Location abelMigwiLab = kenyaEmrService.getLocationByMflCode("10001");
		Assert.assertThat(abelMigwiLab.getName(), is("Abel Migwi Johana Laboratory"));
		Assert.assertThat(abelMigwiLab.getDescription(), is("Laboratory (Stand-alone)"));
		Assert.assertThat(abelMigwiLab.getCountry(), is("Kenya"));
		Assert.assertThat(abelMigwiLab.getStateProvince(), is("Central"));

		// Check no changes if run again
		sync = new FacilityListSynchronization("test-locations.csv", codeAttrType);
		Assert.assertThat(sync.getCreatedCount(), is(0));
		Assert.assertThat(sync.getUpdatedCount(), is(0));
		Assert.assertThat(sync.getRetiredCount(), is(0));
	}
}