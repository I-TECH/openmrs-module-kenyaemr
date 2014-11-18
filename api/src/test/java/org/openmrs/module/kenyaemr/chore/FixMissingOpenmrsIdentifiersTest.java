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
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.PrintWriter;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link FixMissingOpenmrsIdentifiers}
 *
 * TODO make this work. Currently can't work because idgen service won't generate identifiers in tests
 */
@Ignore
public class FixMissingOpenmrsIdentifiersTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private FixMissingOpenmrsIdentifiers chore;

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
	public void perform() throws Exception {
		PatientIdentifierType openmrsIdType = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.OPENMRS_ID);

		chore.perform(new PrintWriter(System.out));

		// Check that all patients have a preferred OpenMRS ID
		for (Patient patient : patientService.getAllPatients()) {
			PatientIdentifier openmrsId = patient.getPatientIdentifier();

			Assert.assertThat(openmrsId.getIdentifierType(), is(openmrsIdType));
			Assert.assertThat(openmrsId.isPreferred(), is(true));
		}
	}
}