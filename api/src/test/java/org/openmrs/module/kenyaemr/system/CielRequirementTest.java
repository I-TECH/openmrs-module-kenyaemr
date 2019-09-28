/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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