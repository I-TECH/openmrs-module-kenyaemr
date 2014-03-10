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
import org.junit.Test;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSession;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link EmrVelocityContentProvider}
 */
public class EmrVelocityContentProviderTest extends BaseModuleContextSensitiveTest {

	/**
	 * @see EmrVelocityContentProvider#populateContext(org.openmrs.module.htmlformentry.FormEntrySession, org.apache.velocity.VelocityContext)
	 */
	@Test
	public void populateContext() throws Exception {
		HttpSession httpSession = new MockHttpSession();
		String formXml = "<htmlform></htmlform>";

		// Create a session for dummy form with patient #6
		FormEntrySession formSession = new FormEntrySession(TestUtils.getPatient(6), formXml, httpSession);

		Assert.assertThat(formSession.evaluateVelocityExpression("$!{kenyaemr}"), startsWith("org.openmrs.module.kenyaemr.form.velocity.EmrVelocityFunctions"));
		Assert.assertThat(formSession.evaluateVelocityExpression("$!{ui}"), startsWith("org.openmrs.module.kenyaemr.form.velocity.UiVelocityFunctions"));
		Assert.assertThat(formSession.evaluateVelocityExpression("$!{Dictionary}"), startsWith("org.apache.velocity.app.FieldMethodizer"));

		Assert.assertThat(formSession.evaluateVelocityExpression("$!{Dictionary.YES}"), is("1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
	}
}