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
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.page.controller.LoginPageController;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.web.controller.patient.PatientDashboardController;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link EmrExternalUrlInterceptor}
 */
public class EmrExternalUrlInterceptorTest extends BaseModuleWebContextSensitiveTest {

	private EmrExternalUrlInterceptor interceptor;

	private MockHttpServletRequest request;

	private MockHttpServletResponse response;

	@Autowired
	private AdministrationService adminService;

	@Autowired
	private KenyaUiUtils kenyaui;

	@Before
	public void setup() {
		interceptor = new EmrExternalUrlInterceptor();
		interceptor.adminService = adminService;
		interceptor.kenyaUi = kenyaui;

		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();

	}

	/**
	 * @see EmrExternalUrlInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, Object)
	 */
	@Test
	public void preHandle_shouldAllowWhitelistedControllersForSuperUser() throws Exception {
		Assert.assertThat(interceptor.preHandle(request, response, new LoginPageController()), is(true));
	}

	/**
	 * @see EmrExternalUrlInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, Object)
	 */
	@Test
	public void preHandle_shouldAllowWhitelistedControllersForNonSuperUser() throws Exception {
		Context.becomeUser("butch");

		Assert.assertThat(interceptor.preHandle(request, response, new LoginPageController()), is(true));
	}

	/**
	 * @see EmrExternalUrlInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, Object)
	 */
	@Test
	public void preHandle_shouldAllowNonWhitelistedControllersForSuperUser() throws Exception {
		Assert.assertThat(interceptor.preHandle(request, response, new PatientDashboardController()), is(true));
	}

	/**
	 * @see EmrExternalUrlInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, Object)
	 */
	@Test
	public void preHandle_shouldRedirectNonWhitelistedControllersForNonSuperUser() throws Exception {
		Context.becomeUser("butch");

		Assert.assertThat(interceptor.preHandle(request, response, new PatientDashboardController()), is(false));
		Assert.assertThat(response.getRedirectedUrl(), is("/login.htm"));
	}

	/**
	 * @see EmrExternalUrlInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, Object)
	 */
	@Test
	public void preHandle_shouldAllowCustomWhitelistedControllersForNonSuperUser() throws Exception {
		TestUtils.saveGlobalProperty(EmrConstants.GP_CONTROLLER_WHITELIST, "org.openmrs.web.controller.patient,org.openmrs.web.controller.visit");

		Context.becomeUser("butch");

		Assert.assertThat(interceptor.preHandle(request, response, new PatientDashboardController()), is(true));
	}
}
