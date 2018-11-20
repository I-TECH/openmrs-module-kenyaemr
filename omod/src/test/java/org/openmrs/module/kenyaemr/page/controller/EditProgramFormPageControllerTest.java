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
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link EditProgramFormPageController}
 */
public class EditProgramFormPageControllerTest extends BaseModuleWebContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	private EditProgramFormPageController controller;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		hivMetadata.install();

		controller = new EditProgramFormPageController();
	}

	/**
	 * @see EditProgramFormPageController#controller(String, org.openmrs.PatientProgram, String, String)
	 */
	@Test
	public void controller() {
		Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
		Form familyHistory = MetadataUtils.existing(Form.class, HivMetadata._Form.FAMILY_HISTORY);

		// Enroll patient #7 in th HIV program from 1-May-2012 to 1-Jun-2012
		PatientProgram enrollment = TestUtils.enrollInProgram(TestUtils.getPatient(7), hivProgram, TestUtils.date(2012, 5, 1), TestUtils.date(2012, 6, 1));

		// Check with no previous submission of the family form
		String result = controller.controller("test.app", enrollment, HivMetadata._Form.FAMILY_HISTORY, "test.html");
		Assert.assertThat(result, is("redirect:kenyaemr/enterForm.page?formUuid=7efa0ee0-6617-4cd7-8310-9f95dfee7a82&appId=test.app&patientId=7&returnUrl=test.html"));

		// Record submission of family history form on day prior to enrollment
		TestUtils.saveEncounter(TestUtils.getPatient(7), familyHistory, TestUtils.date(2012, 4, 30));

		controller.controller("test.app", enrollment, HivMetadata._Form.FAMILY_HISTORY, "test.html");
		Assert.assertThat(result, is("redirect:kenyaemr/enterForm.page?formUuid=7efa0ee0-6617-4cd7-8310-9f95dfee7a82&appId=test.app&patientId=7&returnUrl=test.html"));

		// Record submission of family history form on same day
		Encounter encounter = TestUtils.saveEncounter(TestUtils.getPatient(7), familyHistory, TestUtils.date(2012, 5, 1));

		// And another on day after program completion
		TestUtils.saveEncounter(TestUtils.getPatient(7), familyHistory, TestUtils.date(2012, 6, 2));

		result = controller.controller("test.app", enrollment, HivMetadata._Form.FAMILY_HISTORY, "test.html");
		String expected = "redirect:kenyaemr/editForm.page?encounterId=" + encounter.getId() + "&appId=test.app&patientId=7&returnUrl=test.html";
		Assert.assertThat(result, is(expected));
	}
}