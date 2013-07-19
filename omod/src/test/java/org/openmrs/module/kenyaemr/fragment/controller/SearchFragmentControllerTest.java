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

package org.openmrs.module.kenyaemr.fragment.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.KenyaEmrUiUtils;
import org.openmrs.module.kenyaemr.test.TestUiUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link SearchFragmentController}
 */
public class SearchFragmentControllerTest extends BaseModuleWebContextSensitiveTest {

	private SearchFragmentController controller;

	@Autowired
	private TestUiUtils ui;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");

		controller = new SearchFragmentController();
	}

	@Test
	public void location_shouldSimplifyLocation() {
		Location location = Context.getLocationService().getLocation(1);
		SimpleObject result = controller.location(location, ui);
		Assert.assertThat(result, hasEntry("id", (Object) new Integer(1)));
		Assert.assertThat(result, hasEntry("name", (Object) "Unknown Location"));
		Assert.assertThat(result, hasEntry("code", (Object) "15001"));
	}

	@Test
	public void locations_shouldMatchByPartialName() {
		List<SimpleObject> result = controller.locations("Xan", ui);
		Assert.assertThat(result, hasSize(1));
		Assert.assertThat(result.get(0), hasEntry("id", (Object) new Integer(2)));
		Assert.assertThat(result.get(0), hasEntry("name", (Object) "Xanadu"));
		Assert.assertThat(result.get(0), hasEntry("code", (Object) "15002"));
	}

	@Test
	public void locations_shouldMatchByCompleteMflCode() {
		List<SimpleObject> result = controller.locations("15002", ui);
		Assert.assertThat(result, hasSize(1));
		Assert.assertThat(result.get(0), hasEntry("id", (Object) new Integer(2)));
		Assert.assertThat(result.get(0), hasEntry("name", (Object) "Xanadu"));
		Assert.assertThat(result.get(0), hasEntry("code", (Object) "15002"));
	}
}