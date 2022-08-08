/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.util;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.kenyacore.test.TestUtils;

/**
 * Tests for {@link EmrUtils}
 */
public class BuildPropertiesTest {

	private BuildProperties properties = new BuildProperties();

	/**
	 * @see BuildProperties#setBuildDate(java.util.Date)
	 */
	@Test
	public void setBuildDate() {
		Assert.assertNull(properties.getBuildDate());
		properties.setBuildDate(TestUtils.date(2012, 1, 1));
		Assert.assertEquals(TestUtils.date(2012, 1, 1), properties.getBuildDate());
	}

	/**
	 * @see BuildProperties#setDeveloper(String)
	 */
	@Test
	public void setDeveloper() {
		Assert.assertNull(properties.getDeveloper());
		properties.setDeveloper("Mr Test");
		Assert.assertEquals("Mr Test", properties.getDeveloper());
	}
}