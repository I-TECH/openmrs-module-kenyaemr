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
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.LocationAttributeType;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link LocationMflCsvSource}
 */
public class LocationMflCsvSourceTest {

	@Test
	public void integration() throws Exception {
		LocationAttributeType codeAttrType = new LocationAttributeType();

		LocationMflCsvSource source = new LocationMflCsvSource("test-locations.csv", codeAttrType);

		Location location1 = source.fetchNext();

		Assert.assertThat(location1.getName(), is("Abdisamad Dispensary"));
		Assert.assertThat(location1.getDescription(), is("Dispensary"));

		Assert.assertThat(location1.getAddress5(), is("Sankuri"));
		Assert.assertThat(location1.getAddress6(), is("Garissa"));
		Assert.assertThat(location1.getCountyDistrict(), is("Garissa"));
		Assert.assertThat(location1.getStateProvince(), is("North Eastern"));
		Assert.assertThat(location1.getCountry(), is("Kenya"));
		Assert.assertThat(location1.getPostalCode(), is("70100"));

		Assert.assertThat(location1.getActiveAttributes(codeAttrType), hasSize(1));
		Assert.assertThat(location1.getActiveAttributes(codeAttrType).get(0).getValue(), is((Object) "17009"));

		Location location2 = source.fetchNext();

		Assert.assertThat(location2.getName(), is("Abel Migwi Johana Laboratory"));

		Location location3 = source.fetchNext();

		Assert.assertThat(location3.getName(), is("Aberdare Health Services"));

		Assert.assertThat(source.fetchNext(), nullValue());
	}
}