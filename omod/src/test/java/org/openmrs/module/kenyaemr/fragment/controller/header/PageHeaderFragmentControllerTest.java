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

package org.openmrs.module.kenyaemr.fragment.controller.header;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.kenyaemr.util.BuildProperties;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;

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