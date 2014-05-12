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

package org.openmrs.module.kenyaemr.metadata;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.wrapper.Facility;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Tests for {@link FacilityMetadata}
 */
public class FacilityMetadataTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private FacilityMetadata facilityMetadata;

	@Autowired
	private LocationService locationService;

	@Autowired
	private KenyaEmrService kenyaEmrService;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		commonMetadata.install();
	}

	/**
	 * @see org.openmrs.module.kenyaemr.metadata.FacilityMetadata#install()
	 */
	@Test
	public void install_limited_shouldInstallAttributeTypes() throws Exception {
		facilityMetadata.install(false);

		Assert.assertThat(locationService.getLocationAttributeTypeByUuid(FacilityMetadata._LocationAttributeType.MASTER_FACILITY_CODE), notNullValue());

		Context.flushSession();
	}

	/**
	 * @see org.openmrs.module.kenyaemr.metadata.FacilityMetadata#install()
	 *
	 * Ignored because it takes ~1 min. Should be re-enabled run everytime the MFL CSV resource file is updated to
	 * ensure that the columns haven't changed and that the CSV file is complete.
	 */
	@Ignore
	@Test
	public void install_shouldInstallAllMetadata() throws Exception {
		// Pre-check against standard test data
		List<Location> locations = locationService.getAllLocations(true);
		Assert.assertThat(locations, hasSize(3));

		long start = System.currentTimeMillis();
		facilityMetadata.install();
		long time = System.currentTimeMillis() - start;
		System.out.println("** Loaded locations in " + time + " ms **");

		locations = locationService.getAllLocations(true);
		Assert.assertThat(locations, hasSize(9502));

		// Check a few locations to ensure columns haven't changed
		Facility facility1 = new Facility(kenyaEmrService.getLocationByMflCode("10001"));
		Assert.assertThat(facility1.getTarget().getName(), is("Abel Migwi Johana Laboratory"));
		Assert.assertThat(facility1.getTarget().getDescription(), is("Laboratory (Stand-alone)"));
		Assert.assertThat(facility1.getCountry(), is("Kenya"));
		Assert.assertThat(facility1.getProvince(), is("Central"));
		Assert.assertThat(facility1.getCounty(), is("Kirinyaga"));
		Assert.assertThat(facility1.getDistrict(), is("Kirinyaga West"));
		Assert.assertThat(facility1.getDivision(), is("Ndia"));
		Assert.assertThat(facility1.getPostCode(), is("10100"));
		Assert.assertThat(facility1.getTelephoneMobile(), nullValue());
		Assert.assertThat(facility1.getTelephoneLandline(), nullValue());
		Assert.assertThat(facility1.getTelephoneFax(), nullValue());

		Facility facility2 = new Facility(kenyaEmrService.getLocationByMflCode("10002"));
		Assert.assertThat(facility2.getTarget().getName(), is("Aberdare Medical & Surgical Clinic"));
		Assert.assertThat(facility2.getTarget().getDescription(), is("Medical Clinic"));
		Assert.assertThat(facility2.getCountry(), is("Kenya"));
		Assert.assertThat(facility2.getProvince(), is("Central"));
		Assert.assertThat(facility2.getCounty(), is("Nyeri"));
		Assert.assertThat(facility2.getDistrict(), is("Nyeri South"));
		Assert.assertThat(facility2.getDivision(), is("Othaya"));
		Assert.assertThat(facility2.getPostCode(), is("10100"));
		Assert.assertThat(facility2.getTelephoneMobile(), is("0721-348224"));
		Assert.assertThat(facility2.getTelephoneLandline(), nullValue());
		Assert.assertThat(facility2.getTelephoneFax(), nullValue());

		Context.flushSession();
		Context.clearSession();

		// Install again...
		start = System.currentTimeMillis();
		facilityMetadata.install();
		time = System.currentTimeMillis() - start;
		System.out.println("** Loaded locations in " + time + " ms **");
	}
}