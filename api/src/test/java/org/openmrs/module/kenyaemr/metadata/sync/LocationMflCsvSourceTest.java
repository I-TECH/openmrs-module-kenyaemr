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
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.module.kenyaemr.metadata.FacilityMetadata;
import org.openmrs.module.kenyaemr.wrapper.Facility;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

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