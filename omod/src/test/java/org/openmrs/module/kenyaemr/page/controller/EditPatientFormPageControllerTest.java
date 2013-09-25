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

package org.openmrs.module.kenyaemr.page.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link EditPatientFormPageController}
 */
public class EditPatientFormPageControllerTest extends BaseModuleWebContextSensitiveTest {

	private EditPatientFormPageController controller;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");

		controller = new EditPatientFormPageController();
	}

	/**
	 * @see org.openmrs.module.kenyaemr.page.controller.EditProgramFormPageController#controller(String, org.openmrs.PatientProgram, String, String)
	 */
	@Test
	public void controller() {
		Form familyHistory = MetadataUtils.getForm(HivMetadata._Form.FAMILY_HISTORY);

		// Check with no previous submission of the family form
		String result = controller.controller("test.app", TestUtils.getPatient(7), HivMetadata._Form.FAMILY_HISTORY, "test.html");
		Assert.assertThat(result, is("redirect:kenyaemr/enterForm.page?formUuid=7efa0ee0-6617-4cd7-8310-9f95dfee7a82&appId=test.app&patientId=7&returnUrl=test.html"));

		// Record submission of family history
		Encounter encounter = TestUtils.saveEncounter(TestUtils.getPatient(7), familyHistory, TestUtils.date(2012, 4, 30));

		result = controller.controller("test.app", TestUtils.getPatient(7), HivMetadata._Form.FAMILY_HISTORY, "test.html");
		String expected = "redirect:kenyaemr/editForm.page?encounterId=" + encounter.getId() + "&appId=test.app&patientId=7&returnUrl=test.html";
		Assert.assertThat(result, is(expected));
	}
}