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