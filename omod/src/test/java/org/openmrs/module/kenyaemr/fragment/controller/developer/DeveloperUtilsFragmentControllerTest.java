/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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