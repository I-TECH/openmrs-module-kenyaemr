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
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.page.controller.LoginPageController;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.web.controller.patient.PatientDashboardController;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link EmrExternalUrlInterceptor}
 */
public class EmrExternalUrlInterceptorTest extends BaseModuleWebContextSensitiveTest {

	private EmrExternalUrlInterceptor interceptor = new EmrExternalUrlInterceptor();

	@Autowired
	private AdministrationService adminService;

	@Autowired
	private KenyaUiUtils kenyaui;

	@Before
	public void setup() {
		interceptor.adminService = adminService;
		interceptor.kenyaUi = kenyaui;
	}

	/**
	 * @see EmrExternalUrlInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, Object)
	 */
	@Test
	public void preHandle_shouldAllowWhitelistedControllersForSuperUser() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();

		Assert.assertThat(interceptor.preHandle(request, response, new LoginPageController()), is(true));
	}

	/**
	 * @see EmrExternalUrlInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, Object)
	 */
	@Test
	public void preHandle_shouldAllowWhitelistedControllersForNonSuperUser() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();

		Context.becomeUser("butch");

		Assert.assertThat(interceptor.preHandle(request, response, new LoginPageController()), is(true));
	}

	/**
	 * @see EmrExternalUrlInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, Object)
	 */
	@Test
	public void preHandle_shouldAllowNonWhitelistedControllersForSuperUser() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();

		Assert.assertThat(interceptor.preHandle(request, response, new PatientDashboardController()), is(true));
	}

	/**
	 * @see EmrExternalUrlInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, Object)
	 */
	@Test
	public void preHandle_shouldRedirectNonWhitelistedControllersForNonSuperUser() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		Context.becomeUser("butch");

		Assert.assertThat(interceptor.preHandle(request, response, new PatientDashboardController()), is(false));
		Assert.assertThat(response.getRedirectedUrl(), is("/login.htm"));
	}
}
