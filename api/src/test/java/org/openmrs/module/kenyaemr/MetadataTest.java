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

package org.openmrs.module.kenyaemr;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests for {@link Metadata}
 */
public class MetadataTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
	}

	@Test
	public void integration() {
		new Metadata();
	}

	/**
	 * @see Metadata#getEncounterType(String)
	 */
	@Test
	public void getEncounterType_shouldFetchByMappingOrUuid() {
		Assert.assertNotNull(Metadata.getEncounterType(Metadata.TB_SCREENING_ENCOUNTER_TYPE));
	}

	/**
	 * @see Metadata#getEncounterType(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getEncounterType_shouldThrowExceptionForNonExistent() {
		Metadata.getEncounterType("XXXXX");
	}

	/**
	 * @see Metadata#getLocation(String)
	 */
	@Test
	public void getLocation_shouldFetchByMappingOrUuid() {
		Assert.assertNotNull(Metadata.getLocation(Metadata.UNKNOWN_LOCATION));
	}

	/**
	 * @see Metadata#getLocation(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getLocation_shouldThrowExceptionForNonExistent() {
		Metadata.getLocation("XXXXX");
	}

	/**
	 * @see Metadata#getLocationAttributeType(String)
	 */
	@Test
	public void getLocationAttributeType_shouldFetchByMappingOrUuid() {
		Assert.assertNotNull(Metadata.getLocationAttributeType(Metadata.MASTER_FACILITY_CODE_LOCATION_ATTRIBUTE_TYPE));
	}

	/**
	 * @see Metadata#getLocationAttributeType(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getLocationAttributeType_shouldThrowExceptionForNonExistent() {
		Metadata.getLocationAttributeType("XXXXX");
	}

	/**
	 * @see Metadata#getProgram(String)
	 */
	@Test
	public void getProgram_shouldFetchByMappingOrUuid() {
		Assert.assertNotNull(Metadata.getProgram(Metadata.TB_PROGRAM));
	}

	/**
	 * @see Metadata#getProgram(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getProgram_shouldThrowExceptionForNonExistent() {
		Metadata.getProgram("XXXXX");
	}

	/**
	 * @see Metadata#getVisitType(String)
	 */
	@Test
	public void getVisitType_shouldFetchByMappingOrUuid() {
		Assert.assertNotNull(Metadata.getVisitType(Metadata.OUTPATIENT_VISIT_TYPE));
	}

	/**
	 * @see Metadata#getVisitType(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getVisitType_shouldThrowExceptionForNonExistent() {
		Metadata.getVisitType("XXXXX");
	}
}