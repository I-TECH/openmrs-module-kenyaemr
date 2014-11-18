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
import org.junit.runner.RunWith;
import org.openmrs.api.context.Context;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.Matchers.*;

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