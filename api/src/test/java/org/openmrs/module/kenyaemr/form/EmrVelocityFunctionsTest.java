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

package org.openmrs.module.kenyaemr.form;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.kenyaemr.form.EmrVelocityFunctions;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSession;

public class EmrVelocityFunctionsTest extends BaseModuleContextSensitiveTest {

	private EmrVelocityFunctions functionsForSession1, functionsForSession2;

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");

		HttpSession httpSession = new MockHttpSession();
		String formXml = "<htmlform></htmlform>";

		// Create a session for dummy form with patient #6
		Patient patient6 = Context.getPatientService().getPatient(6);
		FormEntrySession formSession1 = new FormEntrySession(patient6, formXml, httpSession);
		functionsForSession1 = new EmrVelocityFunctions(formSession1);

		// Create a session for dummy form with patient #7
		Patient patient7 = Context.getPatientService().getPatient(7);
		FormEntrySession formSession2 = new FormEntrySession(patient7, formXml, httpSession);
		functionsForSession2 = new EmrVelocityFunctions(formSession2);
	}

	/**
	 * @see EmrVelocityFunctions#hasHivUniquePatientNumber()
	 */
	@Test
	public void hasHivUniquePatientNumber() {
		// Patient #6 doesn't have a UPN
		Assert.assertFalse(functionsForSession1.hasHivUniquePatientNumber());

		// Patient #7 has a UPN
		Assert.assertTrue(functionsForSession2.hasHivUniquePatientNumber());
	}
}