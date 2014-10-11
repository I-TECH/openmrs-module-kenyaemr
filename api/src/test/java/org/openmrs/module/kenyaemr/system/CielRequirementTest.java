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

package org.openmrs.module.kenyaemr.system;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link CielRequirement}
 */
public class CielRequirementTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CielRequirement cielRequirement;

	/**
	 * @see CielRequirement#getName()
	 */
	@Test
	public void getName() {
		Assert.assertNotNull(cielRequirement.getName());
	}

	/**
	 * @see CielRequirement#getRequiredVersion()
	 */
	@Test
	public void getRequiredVersion() {
		Assert.assertNotNull(cielRequirement.getRequiredVersion());
	}

	/**
	 * @see CielRequirement#getFoundVersion()
	 */
	@Test
	public void getFoundVersion() {
		Assert.assertNull(cielRequirement.getFoundVersion());

		TestUtils.saveGlobalProperty("ciel.conceptsVersion", "20130101");

		Assert.assertEquals("20130101", cielRequirement.getFoundVersion());
	}

	/**
	 * @see CielRequirement#isSatisfied()
	 */
	@Test
	public void isSatisfied() {
		Assert.assertFalse(cielRequirement.isSatisfied());

		TestUtils.saveGlobalProperty("ciel.conceptsVersion", cielRequirement.getRequiredVersion());

		Assert.assertTrue(cielRequirement.isSatisfied());
	}

	/**
	 * @see CielRequirement#checkCielVersions(String, String)
	 */
	@Test
	public void checkCielVersions_shouldReturnFalseIfVersionIsNotParseable() {
		Assert.assertFalse(CielRequirement.checkCielVersions("20130101", null));
		Assert.assertFalse(CielRequirement.checkCielVersions("20130101", "x"));
	}

	/**
	 * @see CielRequirement#checkCielVersions(String, String)
	 */
	@Test
	public void checkCielVersions_shouldReturnTrueIfFoundVersionIsGreaterOrEqual() {
		Assert.assertFalse(CielRequirement.checkCielVersions("20130101", "20121201"));
		Assert.assertTrue(CielRequirement.checkCielVersions("20130101", "20130101"));
		Assert.assertTrue(CielRequirement.checkCielVersions("20130101", "20130102"));
	}
}