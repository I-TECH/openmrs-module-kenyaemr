/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.patient;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.calculation.CalculationManager;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.metadata.TbMetadata;
import org.openmrs.module.kenyaemr.test.TestUiUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

/**
 * Tests for {@link PatientUtilsFragmentController}
 */
public class PatientUtilsFragmentControllerTest extends BaseModuleWebContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private TbMetadata tbMetadata;

	@Autowired
	private MchMetadata mchMetadata;

	@Autowired
	private CalculationManager calculationManager;

	@Autowired
	private TestUiUtils ui;

	private PatientUtilsFragmentController controller;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		hivMetadata.install();
		tbMetadata.install();
		mchMetadata.install();

		controller = new PatientUtilsFragmentController();

		calculationManager.refresh();
	}

	/**
	 * @see PatientUtilsFragmentController#age(org.openmrs.Patient, java.util.Date)
	 */
	@Test
	public void age_shouldCalculatePatientAgeOnDate() {
		Patient patient = TestUtils.getPatient(7);
		patient.setBirthdate(TestUtils.date(2000, 1, 1));

		SimpleObject response = controller.age(patient, TestUtils.date(2010, 1, 1)); // Would be exactly 10
		Assert.assertEquals(10, response.get("age"));
		response = controller.age(patient, TestUtils.date(2010, 6, 1)); // Would be 10.5 years
		Assert.assertEquals(10, response.get("age"));
	}

	/**
	 * @see PatientUtilsFragmentController#getFlags(Integer, org.openmrs.module.kenyacore.calculation.CalculationManager)
	 */
	@Test
	public void getFlags_shouldReturnAllFlags() {
		List<SimpleObject> flags = controller.getFlags(7, calculationManager);

		// Check that every flag object has a message and that it doesn't start with "ERROR..."
	 	for (SimpleObject flag : flags) {
			TestUtils.printJson(flag);

			Assert.assertThat(flag, hasKey("message"));
			Assert.assertThat((String) flag.get("message"), not(startsWith("ERROR")));
		}
	}

	/**
	 * @see PatientUtilsFragmentController#getMothers(org.openmrs.Patient, org.openmrs.ui.framework.UiUtils)
	 */
	@Test
	public void getMothers_shouldReturnAllMothersOfPatient() {
		Patient parent = TestUtils.getPatient(7); // female in standardTestDataset.xml
		Patient child = TestUtils.getPatient(2);

		// Save parent-child relationship between patient #2 and #7
		TestUtils.saveRelationship(parent, Context.getPersonService().getRelationshipType(2), child);

		SimpleObject[] mothers = controller.getMothers(child, ui);

		// Check patient #7 is returned as sole mother
		Assert.assertThat(mothers, arrayWithSize(1));
		Assert.assertThat(mothers[0], hasEntry("id", (Object) (7)));
	}

	/**
	 * @see PatientUtilsFragmentController#getFathers(org.openmrs.Patient, org.openmrs.ui.framework.UiUtils)
	 */
	@Test
	public void getFathers_shouldReturnAllFathersOfPatient() {
		Patient parent = TestUtils.getPatient(6); // male in standardTestDataset.xml
		Patient child = TestUtils.getPatient(2);

		// Save parent-child relationship between patient #2 and #7
		TestUtils.saveRelationship(parent, Context.getPersonService().getRelationshipType(2), child);

		SimpleObject[] fathers = controller.getFathers(child, ui);

		// Check patient #6 is returned as sole father
		Assert.assertThat(fathers, arrayWithSize(1));
		Assert.assertThat(fathers[0], hasEntry("id", (Object) (6)));
	}
	/**
	 * @see PatientUtilsFragmentController#getGuardians(org.openmrs.Patient, org.openmrs.ui.framework.UiUtils)
	 *
	@Test
	public void getGuardians_shouldReturnAllGuardiansOfPatient() {
		Patient guardian = TestUtils.getPatient(6); // male in standardTestDataset.xml
		Patient child = TestUtils.getPatient(2);

		// Save guardian-child relationship between patient #2 and #7
		TestUtils.saveRelationship(guardian, Context.getPersonService().getRelationshipType(2), child);

		SimpleObject[] guardians = controller.getGuardians(child, ui);

		// Check patient #6 is returned as sole guardian
		Assert.assertThat(guardian, arrayWithSize(1));
		Assert.assertThat(guardians[0], hasEntry("id", (Object) (6)));
	}
	*/
}