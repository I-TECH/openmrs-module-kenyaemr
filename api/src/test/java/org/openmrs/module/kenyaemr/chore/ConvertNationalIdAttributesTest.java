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

package org.openmrs.module.kenyaemr.chore;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.test.StandardTestData;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.bundle.CoreConstructors;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.PrintWriter;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Tests for {@link ConvertNationalIdAttributes}
 */
public class ConvertNationalIdAttributesTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private ConvertNationalIdAttributes chore;

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private PatientService patientService;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() {
		commonMetadata.install();
	}

	/**
	 * @see FixMissingOpenmrsIdentifiers#perform(java.io.PrintWriter)
	 */
	@Test
	public void perform_onSystemWithoutExistingAttributes() {
		chore.perform(new PrintWriter(System.out)); // Does nothing
	}

	/**
	 * @see FixMissingOpenmrsIdentifiers#perform(java.io.PrintWriter)
	 */
	@Test
	public void perform_onSystemWithExistingAttributes() {
		PersonAttributeType nationalIdAttrType = CoreConstructors.personAttributeType("Test", "Testing", String.class, null, false, 1.0, "73d34479-2f9e-4de3-a5e6-1f79a17459bb");
		Context.getPersonService().savePersonAttributeType(nationalIdAttrType);

		// All patients need an OpenMRS ID to be valid
		addMissingOpenmrsIdentifiers();

		// Save some national ID attributes
		TestUtils.getPatient(6).addAttribute(new PersonAttribute(nationalIdAttrType, "000006"));
		TestUtils.getPatient(7).addAttribute(new PersonAttribute(nationalIdAttrType, "000007"));

		PatientIdentifierType nationalIdType = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.NATIONAL_ID);

		// Give patient #2 an existing national ID identifier that conflicts with patient #6's attribute
		TestUtils.savePatientIdentifier(TestUtils.getPatient(2), nationalIdType, "000006");

		chore.perform(new PrintWriter(System.out));

		Assert.assertThat(TestUtils.getPatient(2).getPatientIdentifier(nationalIdType).getIdentifier(), is("000006"));
		Assert.assertThat(TestUtils.getPatient(6).getPatientIdentifier(nationalIdType), nullValue()); // due to conflict with #2
		Assert.assertThat(TestUtils.getPatient(7).getPatientIdentifier(nationalIdType).getIdentifier(), is("000007"));
		Assert.assertThat(TestUtils.getPatient(8).getPatientIdentifier(nationalIdType), nullValue());
	}

	/**
	 * Can't save patients unless they have required OpenMRS IDs
	 */
	private void addMissingOpenmrsIdentifiers() {
		PatientIdentifierType openmrsIDType = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.OPENMRS_ID);
		Location unknown = MetadataUtils.existing(Location.class, StandardTestData._Location.UNKNOWN);
		OpenmrsIDSource source = new OpenmrsIDSource();

		for (Patient patient : patientService.getAllPatients()) {
			PatientIdentifier identifier = new PatientIdentifier(source.next(), openmrsIDType, unknown);

			if (patient.getPatientIdentifier() == null || !patient.getPatientIdentifier().isPreferred()) {
				identifier.setPreferred(true);
			}

			patient.addIdentifier(identifier);
		}
	}

	/**
	 * Helper class to make valid OpenMRS ID identifier values without using IDGEN
	 */
	private static class OpenmrsIDSource {

		private int index = 0;
		private static String[] OPENMRS_IDS = { "M3G", "M4E", "M6C", "M79", "M96", "MA3", "MDV", "MET" };

		public String next() {
			return OPENMRS_IDS[index++];
		}
	}
}