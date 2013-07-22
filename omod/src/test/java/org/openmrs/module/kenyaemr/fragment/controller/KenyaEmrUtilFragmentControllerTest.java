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

package org.openmrs.module.kenyaemr.fragment.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.CoreContext;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link KenyaEmrUtilFragmentController}
 */
public class KenyaEmrUtilFragmentControllerTest extends BaseModuleWebContextSensitiveTest {

	private KenyaEmrUtilFragmentController controller;

	@Autowired
	private CoreContext emr;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");

		controller = new KenyaEmrUtilFragmentController();
	}

	/**
	 * @see KenyaEmrUtilFragmentController#age(org.openmrs.Patient, java.util.Date)
	 */
	@Test
	public void age_shouldCalculatePatientAgeOnDate() {
		Patient patient = Context.getPatientService().getPatient(7);
		patient.setBirthdate(TestUtils.date(2000, 1, 1));

		SimpleObject response = controller.age(patient, TestUtils.date(2010, 1, 1)); // Would be exactly 10
		Assert.assertEquals(10, response.get("age"));
		response = controller.age(patient, TestUtils.date(2010, 6, 1)); // Would be 10.5 years
		Assert.assertEquals(10, response.get("age"));
	}
}