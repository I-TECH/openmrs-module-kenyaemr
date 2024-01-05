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
import org.junit.runner.RunWith;
import org.openmrs.api.context.Context;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link EmrOpenmrsUrlOverrideController]}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class EmrOpenmrsUrlOverrideControllerTest {

	private EmrOpenmrsUrlOverrideController controller;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() {
		controller = new EmrOpenmrsUrlOverrideController();

		PowerMockito.mockStatic(Context.class);
	}

	/**
	 * @see EmrOpenmrsUrlOverrideController#showOurHomePage()
	 */
	@Test
	public void showOurHomepage() {
		PowerMockito.when(Context.isAuthenticated()).thenReturn(false);
		Assert.assertThat(controller.showOurHomePage(), is("redirect:/login.htm"));

		PowerMockito.when(Context.isAuthenticated()).thenReturn(true);
		Assert.assertThat(controller.showOurHomePage(), is("forward:/kenyaemr/home.page"));
	}

	/**
	 * @see EmrOpenmrsUrlOverrideController#showOurLoginPage()
	 */
	@Test
	public void showLoginHomepage() {
		PowerMockito.when(Context.isAuthenticated()).thenReturn(false);
		Assert.assertThat(controller.showOurLoginPage(), is("forward:/kenyaemr/login.page"));

		PowerMockito.when(Context.isAuthenticated()).thenReturn(true);
		Assert.assertThat(controller.showOurLoginPage(), is("redirect:/index.htm"));
	}

	/**
	 * @see EmrOpenmrsUrlOverrideController#showOurForgotPasswordPage()
	 */
	@Test
	public void showOurForgotPasswordPage() {
		PowerMockito.when(Context.isAuthenticated()).thenReturn(false);
		Assert.assertThat(controller.showOurForgotPasswordPage(), is("forward:/kenyaemr/forgotPassword.page"));

		PowerMockito.when(Context.isAuthenticated()).thenReturn(true);
		Assert.assertThat(controller.showOurForgotPasswordPage(), is("redirect:/index.htm"));
	}
}