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
import org.openmrs.LocationAttributeType;
import org.openmrs.module.kenyaemr.metadata.FacilityMetadata;
import org.openmrs.module.kenyaemr.wrapper.Facility;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link LocationMflCsvSource}
 */
public class LocationMflCsvSourceTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private FacilityMetadata facilityMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		facilityMetadata.install(false);
	}

	@Test
	public void integration() throws Exception {
		LocationMflCsvSource source = new LocationMflCsvSource("test-locations.csv");

		Location location1 = source.fetchNext();
		Facility facility1 = new Facility(location1);

		Assert.assertThat(location1.getName(), is("Abdisamad Dispensary"));
		Assert.assertThat(location1.getDescription(), is("Dispensary"));

		Assert.assertThat(facility1.getMflCode(), is("17009"));
		Assert.assertThat(facility1.getCountry(), is("Kenya"));
		Assert.assertThat(facility1.getProvince(), is("North Eastern"));
		Assert.assertThat(facility1.getCounty(), is("Garissa"));
		Assert.assertThat(facility1.getDistrict(), is("Garissa"));
		Assert.assertThat(facility1.getDivision(), is("Sankuri"));
		Assert.assertThat(facility1.getTelephoneLandline(), is("0462103570"));
		Assert.assertThat(facility1.getTelephoneMobile(), nullValue());
		Assert.assertThat(facility1.getPostCode(), is("70100"));

		Location location2 = source.fetchNext();

		Assert.assertThat(location2.getName(), is("Abel Migwi Johana Laboratory"));

		Location location3 = source.fetchNext();

		Assert.assertThat(location3.getName(), is("Aberdare Health Services"));

		Assert.assertThat(source.fetchNext(), nullValue());
	}
}