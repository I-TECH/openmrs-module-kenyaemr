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

package org.openmrs.module.kenyacore.metadata;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link MetadataUtils}
 */
public class MetadataUtilsTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
	}

	@Test
	public void integration() {
		new MetadataUtils();
	}

	/**
	 * @see MetadataUtils#getEncounterType(String)
	 */
	@Test
	public void getEncounterType_shouldFetchByMappingOrUuid() {
		Assert.assertNotNull(MetadataUtils.getEncounterType("07000be2-26b6-4cce-8b40-866d8435b613")); // Emergency
	}

	/**
	 * @see MetadataUtils#getEncounterType(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getEncounterType_shouldThrowExceptionForNonExistent() {
		MetadataUtils.getEncounterType("XXXXX");
	}

	/**
	 * @see MetadataUtils#getLocation(String)
	 */
	@Test
	public void getLocation_shouldFetchByMappingOrUuid() {
		Assert.assertNotNull(MetadataUtils.getLocation("dc5c1fcc-0459-4201-bf70-0b90535ba362")); // Unknown Location
	}

	/**
	 * @see MetadataUtils#getLocation(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getLocation_shouldThrowExceptionForNonExistent() {
		MetadataUtils.getLocation("XXXXX");
	}

	/**
	 * @see MetadataUtils#getLocationAttributeType(String)
	 */
	@Test
	public void getLocationAttributeType_shouldFetchByMappingOrUuid() {
		// No location attribute type in the standard test data so make one..
		LocationAttributeType phoneAttrType = new LocationAttributeType();
		phoneAttrType.setName("Facility Phone");
		phoneAttrType.setMinOccurs(0);
		phoneAttrType.setMaxOccurs(1);
		phoneAttrType.setDatatypeClassname("org.openmrs.customdatatype.datatype.FreeTextDatatype");
		Context.getLocationService().saveLocationAttributeType(phoneAttrType);
		String savedUuid = phoneAttrType.getUuid();

		Assert.assertThat(MetadataUtils.getLocationAttributeType(savedUuid), is(phoneAttrType));
	}

	/**
	 * @see MetadataUtils#getLocationAttributeType(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getLocationAttributeType_shouldThrowExceptionForNonExistent() {
		MetadataUtils.getLocationAttributeType("XXXXX");
	}

	/**
	 * @see MetadataUtils#getProgram(String)
	 */
	@Test
	public void getProgram_shouldFetchByMappingOrUuid() {
		Assert.assertNotNull(MetadataUtils.getProgram("da4a0391-ba62-4fad-ad66-1e3722d16380")); // HIV
	}

	/**
	 * @see MetadataUtils#getProgram(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getProgram_shouldThrowExceptionForNonExistent() {
		MetadataUtils.getProgram("XXXXX");
	}

	/**
	 * @see MetadataUtils#getVisitType(String)
	 */
	@Test
	public void getVisitType_shouldFetchByMappingOrUuid() {
		Assert.assertNotNull(MetadataUtils.getVisitType("c0c579b0-8e59-401d-8a4a-976a0b183519")); // Initial
	}

	/**
	 * @see MetadataUtils#getVisitType(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getVisitType_shouldThrowExceptionForNonExistent() {
		MetadataUtils.getVisitType("XXXXX");
	}
}