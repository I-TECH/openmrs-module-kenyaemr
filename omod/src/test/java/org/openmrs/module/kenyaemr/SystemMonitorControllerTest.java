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