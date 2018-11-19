/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link EmrUtilsFragmentController}
 */
public class EmrUtilsFragmentControllerTest extends BaseModuleWebContextSensitiveTest {

	private EmrUtilsFragmentController controller;

	@Autowired
	private KenyaUiUtils kenyaui;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		controller = new EmrUtilsFragmentController();
	}

	/**
	 * @see EmrUtilsFragmentController#birthdateFromAge(Integer, java.util.Date, org.openmrs.module.kenyaui.KenyaUiUtils)
	 */
	@Test
	public void birthdateFromAge_shouldCalculateBirthdate() {
		Assert.assertThat(controller.birthdateFromAge(10, null, kenyaui), hasKey("birthdate"));
		Assert.assertThat(controller.birthdateFromAge(0, TestUtils.date(2000, 6, 1), kenyaui), hasEntry("birthdate", (Object) "2000-06-01"));
		Assert.assertThat(controller.birthdateFromAge(10, TestUtils.date(2000, 6, 1), kenyaui), hasEntry("birthdate", (Object) "1990-06-01"));
	}
}