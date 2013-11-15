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
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests for {@link SystemInformation}
 */
public class SystemInformationTest extends BaseModuleContextSensitiveTest {

	/**
	 * @see SystemInformation#getModuleVersion()
	 *
	 * TODO figure out how we can mock ModuleFactory from a BaseModuleContextSensitiveTest
	 */
	@Ignore
	public void getModuleVersion() {
		SystemInformation.getModuleVersion();
	}

	/**
	 * @see SystemInformation#getModuleBuildProperties()
	 * @verifies return build properties
	 */
	@Test
	public void getModuleBuildProperties_shouldGetBuildProperties() {
		BuildProperties properties = SystemInformation.getModuleBuildProperties();

		Assert.assertNotNull(properties);
		Assert.assertNotNull(properties.getBuildDate());
		Assert.assertNotNull(properties.getDeveloper());
	}
}