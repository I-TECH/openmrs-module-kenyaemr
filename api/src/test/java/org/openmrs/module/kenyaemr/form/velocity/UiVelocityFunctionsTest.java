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

package org.openmrs.module.kenyaemr.form.velocity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.kenyaemr.test.TestUiUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
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