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
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link EditPatientFormPageController}
 */
public class EditPatientFormPageControllerTest extends BaseModuleWebContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	private EditPatientFormPageController controller;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		commonMetadata.install();

		controller = new EditPatientFormPageController();
	}

	/**
	 * @see org.openmrs.module.kenyaemr.page.controller.EditProgramFormPageController#controller(String, org.openmrs.PatientProgram, String, String)
	 */
	@Test
	public void controller() {
		Form obsHistory = MetadataUtils.existing(Form.class, CommonMetadata._Form.OBSTETRIC_HISTORY);

		// Check with no previous submission of the obstetric history form
		String result = controller.controller("test.app", TestUtils.getPatient(7), CommonMetadata._Form.OBSTETRIC_HISTORY, "test.html");
		Assert.assertThat(result, is("redirect:kenyaemr/enterForm.page?formUuid=8e4e1abf-7c08-4ba8-b6d8-19a9f1ccb6c9&appId=test.app&patientId=7&returnUrl=test.html"));

		// Record submission of obstetric history
		Encounter encounter = TestUtils.saveEncounter(TestUtils.getPatient(7), obsHistory, TestUtils.date(2012, 4, 30));

		result = controller.controller("test.app", TestUtils.getPatient(7), CommonMetadata._Form.OBSTETRIC_HISTORY, "test.html");
		String expected = "redirect:kenyaemr/editForm.page?encounterId=" + encounter.getId() + "&appId=test.app&patientId=7&returnUrl=test.html";
		Assert.assertThat(result, is(expected));
	}
}