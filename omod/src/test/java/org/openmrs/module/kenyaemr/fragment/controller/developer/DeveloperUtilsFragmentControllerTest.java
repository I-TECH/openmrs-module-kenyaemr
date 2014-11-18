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

package org.openmrs.module.kenyaemr.fragment.controller.developer;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.module.reporting.evaluation.EvaluationProfiler;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link DeveloperUtilsFragmentController}
 */
public class DeveloperUtilsFragmentControllerTest extends BaseModuleWebContextSensitiveTest {

	private DeveloperUtilsFragmentController controller;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		controller = new DeveloperUtilsFragmentController();
	}

	/**
	 * TODO figure out why this throws "Service not found: interface org.openmrs.logic.LogicService"
	 *
	 * @see DeveloperUtilsFragmentController#executeGroovy(String)
	 */
	@Ignore
	@Test
	public void executeGroovy() {
		SimpleObject response = controller.executeGroovy("def x = 10");

		Assert.assertThat(response, hasEntry("result", (Object) 10));
	}

	/**
	 * @see DeveloperUtilsFragmentController#getReportProfilingEnabled()
	 */
	@Test
	public void getReportProfilingEnabled() {
		Assert.assertThat(controller.getReportProfilingEnabled(), hasEntry("enabled", (Object) Boolean.FALSE));
	}

	/**
	 * @see DeveloperUtilsFragmentController#setReportProfilingEnabled(boolean)
	 */
	@Test
	public void setReportProfilingEnabled() {
		Level oldProfilerLevel = LogManager.getLogger(EvaluationProfiler.class).getLevel();
		Level oldServiceLevel = LogManager.getLogger("org.openmrs.api").getLevel();

		controller.setReportProfilingEnabled(true);
		Assert.assertThat(LogManager.getLogger(EvaluationProfiler.class).getLevel(), is(Level.TRACE));
		Assert.assertThat(LogManager.getLogger("org.openmrs.api").getLevel(), is(Level.WARN));

		controller.setReportProfilingEnabled(false);
		Assert.assertThat(LogManager.getLogger(EvaluationProfiler.class).getLevel(), nullValue());
		Assert.assertThat(LogManager.getLogger("org.openmrs.api").getLevel(), is(Level.INFO));

		// Restore old levels
		LogManager.getLogger(EvaluationProfiler.class).setLevel(oldProfilerLevel);
		LogManager.getLogger("org.openmrs.api").setLevel(oldServiceLevel);
	}
}