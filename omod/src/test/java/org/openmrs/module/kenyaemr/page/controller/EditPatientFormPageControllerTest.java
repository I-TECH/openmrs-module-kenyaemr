/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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