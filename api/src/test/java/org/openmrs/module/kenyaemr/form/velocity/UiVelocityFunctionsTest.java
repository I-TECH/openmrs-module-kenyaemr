/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.form.velocity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.test.TestUiUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.ui.framework.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSession;

import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link UiVelocityFunctions}
 */
public class UiVelocityFunctionsTest extends BaseModuleContextSensitiveTest {

	private UiVelocityFunctions functions;

	@Autowired
	private TestUiUtils ui;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		HttpSession httpSession = new MockHttpSession();
		String formXml = "<htmlform></htmlform>";

		// Create a session for dummy form with patient #7
		FormEntrySession formSession = new FormEntrySession(TestUtils.getPatient(7), formXml, httpSession);
		functions = new UiVelocityFunctions(formSession, ui);
	}

	/**
	 * @see org.openmrs.module.kenyaemr.form.velocity.UiVelocityFunctions#resourceLink(String, String)
	 */
	@Test
	public void resourceLink() {
		WebConstants.CONTEXT_PATH = "testing";

		Assert.assertThat(functions.resourceLink("kenyaemr", "test.png"), is("/testing/ms/uiframework/resource/kenyaemr/test.png"));
	}
}