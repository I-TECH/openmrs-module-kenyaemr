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
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Map;

import static org.hamcrest.Matchers.hasKey;

/**
 * Tests for {@link ServerInformation}
 */
public class ServerInformationTest extends BaseModuleContextSensitiveTest {

	@Test
	public void integration() {
		new ServerInformation();
	}

	/**
	 * @see ServerInformation#getAllInformation()
	 */
	@Test
	public void getAllInformation_shouldFetchAllServerInformation() {
		Map<String, Object> info = ServerInformation.getAllInformation();

		Assert.assertThat(info, hasKey("system"));
		Assert.assertThat(info, hasKey("runtime"));
		Assert.assertThat(info, hasKey("openmrs"));
		Assert.assertThat(info, hasKey("kenyaemr"));
	}
}