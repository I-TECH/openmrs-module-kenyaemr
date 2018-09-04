/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.wrapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link PatientWrapper}
 */
public class PatientWrapperTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private PatientService patientService;

	private Patient patient;

	private PatientWrapper wrapper;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");

		commonMetadata.install();
		hivMetadata.install();

		patient = TestUtils.getPatient(7);
		wrapper = new PatientWrapper(patient);
	}

	/**
	 * @see PatientWrapper#getPerson()
	 */
	@Test
	public void getPerson_shouldUnderlyingPersonWrapped() {
		Assert.assertThat(wrapper.getPerson().getTarget(), is((OpenmrsObject) patient));
	}

	/**
	 * @see PatientWrapper#getMedicalRecordNumber()
	 */
	@Test
	public void getMedicalRecordNumber_shouldReturnIdentifierOrNull() {
		Assert.assertThat(wrapper.getMedicalRecordNumber(), nullValue());

		PatientIdentifierType openmrsID = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.OPENMRS_ID);
		TestUtils.savePatientIdentifier(patient, openmrsID, "M3G");

		Assert.assertThat(wrapper.getMedicalRecordNumber(), is("M3G"));
	}

	/**
	 * @see PatientWrapper#getPatientClinicNumber()
	 */
	@Test
	public void getPatientClinicNumber_shouldReturnIdentifierOrNull() {
		Assert.assertThat(wrapper.getPatientClinicNumber(), nullValue());

		wrapper.setPatientClinicNumber("12345", null);

		Assert.assertThat(wrapper.getPatientClinicNumber(), is("12345"));
	}

	/**
	 * @see PatientWrapper#getUniquePatientNumber()
	 */
	@Test
	public void getUniquePatientNumber_shouldReturnIdentifierOrNull() {
		Assert.assertThat(wrapper.getUniquePatientNumber(), nullValue());

		wrapper.setUniquePatientNumber("1234512345", null);

		Assert.assertThat(wrapper.getUniquePatientNumber(), is("1234512345"));
	}

	/**
	 * @see PatientWrapper#getNationalIdNumber()
	 */
	@Test
	public void getNationalIdNumber_shouldReturnIdentifierOrNull() {
		Assert.assertThat(wrapper.getNationalIdNumber(), nullValue());

		wrapper.setNationalIdNumber("1234512345", null);

		Assert.assertThat(wrapper.getNationalIdNumber(), is("1234512345"));
	}

	/**
	 * @see PatientWrapper#getNextOfKinAddress()
	 */
	@Test
	public void getNextOfKinAddress_shouldReturnPersonAttributeOrNull() {
		Assert.assertThat(wrapper.getNextOfKinAddress(), nullValue());

		wrapper.setNextOfKinAddress("Nairobi");

		Assert.assertThat(wrapper.getNextOfKinAddress(), is("Nairobi"));
	}

	/**
	 * @see PatientWrapper#getNextOfKinContact()
	 */
	@Test
	public void getNextOfKinContact_shouldReturnPersonAttributeOrNull() {
		Assert.assertThat(wrapper.getNextOfKinContact(), nullValue());

		wrapper.setNextOfKinContact("0123456789");

		Assert.assertThat(wrapper.getNextOfKinContact(), is("0123456789"));
	}

	/**
	 * @see PatientWrapper#getNextOfKinName()
	 */
	@Test
	public void getNextOfKinName_shouldReturnPersonAttributeOrNull() {
		Assert.assertThat(wrapper.getNextOfKinName(), nullValue());

		wrapper.setNextOfKinName("Bob");

		Assert.assertThat(wrapper.getNextOfKinName(), is("Bob"));
	}

	/**
	 * @see PatientWrapper#getNextOfKinRelationship()
	 */
	@Test
	public void getNextOfKinRelationship_shouldReturnPersonAttributeOrNull() {
		Assert.assertThat(wrapper.getNextOfKinRelationship(), nullValue());

		wrapper.setNextOfKinRelationship("Father");

		Assert.assertThat(wrapper.getNextOfKinRelationship(), is("Father"));
	}

	/**
	 * @see PatientWrapper#getSubChiefName()
	 */
	@Test
	public void getSubChiefName_shouldReturnPersonAttributeOrNull() {
		Assert.assertThat(wrapper.getSubChiefName(), nullValue());

		wrapper.setSubChiefName("Dave");

		Assert.assertThat(wrapper.getSubChiefName(), is("Dave"));
	}
}