/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link SystemMonitorController}
 */
public class SystemMonitorControllerTest {

	private SystemMonitorController controller = new SystemMonitorController();

	/**
	 * @see SystemMonitorController#checkAccess(javax.servlet.http.HttpServletRequest)
	 */
	@Test
	public void checkAccess_shouldGrantAccessToLocalRequests() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setLocalAddr("1.2.3.4");
		request.setRemoteAddr("1.2.3.4");

		Assert.assertThat(controller.checkAccess(request), is(true));

		request = new MockHttpServletRequest();
		request.setLocalAddr("5.6.7.8");
		request.setRemoteAddr("1.2.3.4");

		Assert.assertThat(controller.checkAccess(request), is(false));

		request = new MockHttpServletRequest();
		request.setLocalAddr(null);
		request.setRemoteAddr("1.2.3.4");

		Assert.assertThat(controller.checkAccess(request), is(false));
	}
}