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