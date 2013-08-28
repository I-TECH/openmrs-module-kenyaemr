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

/**
 * Tests for {@link CielRequirement}
 */
public class CielRequirementTest {

	@Test
	public void checkCielVersion_shouldReturnFalseIfVersionIsNotParseable() {
		Assert.assertFalse(CielRequirement.checkCielVersions("20130101", null));
		Assert.assertFalse(CielRequirement.checkCielVersions("20130101", "x"));
	}

	@Test
	public void checkCielVersion_shouldReturnTrueIfFoundVersionIsGreaterOrEqual() {
		Assert.assertFalse(CielRequirement.checkCielVersions("20130101", "20121201"));
		Assert.assertTrue(CielRequirement.checkCielVersions("20130101", "20130101"));
		Assert.assertTrue(CielRequirement.checkCielVersions("20130101", "20130102"));
	}
}