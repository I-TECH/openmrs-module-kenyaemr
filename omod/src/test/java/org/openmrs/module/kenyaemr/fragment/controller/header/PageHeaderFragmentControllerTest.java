/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.header;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.kenyaemr.util.BuildProperties;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.startsWith;

/**
 * Tests for {@link PageHeaderFragmentController}
 */
public class PageHeaderFragmentControllerTest extends BaseModuleWebContextSensitiveTest {

	@Autowired
	private KenyaUiUtils kenyaui;

	@Autowired
	private BuildProperties buildProperties;

	private PageHeaderFragmentController controller;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		controller = new PageHeaderFragmentController();
	}

	/**
	 * @see PageHeaderFragmentController#controller(org.openmrs.ui.framework.fragment.FragmentModel, org.openmrs.module.kenyaui.KenyaUiUtils)
	 */
	@Test
	public void controller_shouldInitializeModelForSnapshotVersions() {
		// Fake a snapshot build
		String oldVersion = buildProperties.getVersion();
		buildProperties.setVersion("13.3-SNAPSHOT");

		FragmentModel model = new FragmentModel();

		controller.controller(model, kenyaui);

		Assert.assertThat((String) model.getAttribute("moduleVersion"), startsWith("13.3-SNAPSHOT ("));
		Assert.assertThat(model, hasKey("systemLocation"));
		Assert.assertThat(model, hasKey("systemLocationCode"));

		buildProperties.setVersion(oldVersion);
	}

	/**
	 * @see PageHeaderFragmentController#controller(org.openmrs.ui.framework.fragment.FragmentModel, org.openmrs.module.kenyaui.KenyaUiUtils)
	 */
	@Test
	public void controller_shouldInitializeModelForReleaseVersions() {
		// Fake a non-snapshot build
		String oldVersion = buildProperties.getVersion();
		buildProperties.setVersion("13.3");

		FragmentModel model = new FragmentModel();
		controller.controller(model, kenyaui);

		Assert.assertThat(model, hasEntry("moduleVersion", (Object) "13.3"));
		Assert.assertThat(model, hasKey("systemLocation"));
		Assert.assertThat(model, hasKey("systemLocationCode"));

		buildProperties.setVersion(oldVersion);
	}
}