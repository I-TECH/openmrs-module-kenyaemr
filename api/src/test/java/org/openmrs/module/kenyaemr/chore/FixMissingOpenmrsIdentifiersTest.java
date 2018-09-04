/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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