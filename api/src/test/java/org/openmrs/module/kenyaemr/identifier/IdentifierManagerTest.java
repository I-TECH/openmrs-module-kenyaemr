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

package org.openmrs.module.kenyaemr.identifier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link IdentifierManager}
 */
public class IdentifierManagerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private IdentifierManager identifierManager;

	@Autowired
	private KenyaEmr emr;

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");

		identifierManager.refresh();
	}

	/**
	 * @see IdentifierManager#getPatientDisplayIdentifiers(org.openmrs.Patient)
	 */
	@Test
	public void getPatientDisplayIdentifiers() {
		// Give #6 a single OpenMRS ID
		Context.getPatientService().voidPatientIdentifier(Context.getPatientService().getPatient(6).getPatientIdentifier(), "test");
		PatientIdentifier pidOMRS = TestUtils.savePatientIdentifier(Context.getPatientService().getPatient(6), Metadata.getPatientIdentifierType(Metadata.OPENMRS_ID_IDENTIFIER_TYPE), "M3G");

		List<PatientIdentifier> ids = identifierManager.getPatientDisplayIdentifiers(Context.getPatientService().getPatient(6));
		Assert.assertThat(ids, containsInAnyOrder(pidOMRS));

		// Give #6 additional identifiers
		PatientIdentifier pidUPN = TestUtils.savePatientIdentifier(Context.getPatientService().getPatient(6), Metadata.getPatientIdentifierType(Metadata.UNIQUE_PATIENT_NUMBER_IDENTIFIER_TYPE), "1321200009");
		PatientIdentifier pidPCN = TestUtils.savePatientIdentifier(Context.getPatientService().getPatient(6), Metadata.getPatientIdentifierType(Metadata.PATIENT_CLINIC_NUMBER_IDENTIFIER_TYPE), "4646422");

		ids = identifierManager.getPatientDisplayIdentifiers(Context.getPatientService().getPatient(6));
		Assert.assertThat(ids, containsInAnyOrder(pidUPN, pidPCN));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.identifier.IdentifierManager#getMrnIdentifierSource()
	 */
	@Test
	public void getMrnIdentifierSource_shouldReturnIdentifierSourceIfSetup() {
		identifierManager.setupMrnIdentifierSource("4");

		Assert.assertNotNull(identifierManager.getMrnIdentifierSource());
	}

	/**
	 * @see org.openmrs.module.kenyaemr.identifier.IdentifierManager#getHivUniqueIdentifierSource()
	 */
	@Test
	public void getHivUniqueIdentifierSource_shouldReturnIdentifierSourceIfSetup() {
		identifierManager.setupHivUniqueIdentifierSource("00517");

		Assert.assertNotNull(identifierManager.getHivUniqueIdentifierSource());
	}

	/**
	 * @see IdentifierManager#setupMrnIdentifierSource(String)
	 * @verifies fail if already set up
	 */
	@Test(expected = Exception.class)
	public void setupMrnIdentifierSource_shouldFailIfAlreadySetup() throws Exception {
		identifierManager.setupMrnIdentifierSource("4");
		identifierManager.setupMrnIdentifierSource("4");
	}

	/**
	 * @see IdentifierManager#setupHivUniqueIdentifierSource(String)
	 * @verifies fail if already set up
	 */
	@Test(expected = Exception.class)
	public void setupHivUniqueIdentifierSource_shouldFailIfAlreadySetup() throws Exception {
		identifierManager.setupHivUniqueIdentifierSource("00517");
		identifierManager.setupHivUniqueIdentifierSource("00517");
	}

	/**
	 * @see IdentifierManager#getNextHivUniquePatientNumber(String)
	 * @verifies get sequential numbers with mfl prefix
	 *
	 * TODO latest versions of idgen won't let you setup source and generate identifier in same session. Figure out workaround to enable better unit testing
	 */
	/*@Test
	public void getNextHivUniquePatientNumber_shouldGetSequentialNumbersWithMflPrefix() throws Exception {
		Location loc = Context.getLocationService().getLocation(1);
		Assert.assertNotNull(loc);
		Context.getService(KenyaEmrService.class).setDefaultLocation(loc);

		identifierManager.setupHivUniqueIdentifierSource("00571");
		Assert.assertEquals("1500100571", identifierManager.getNextHivUniquePatientNumber(null));
		Assert.assertEquals("1500100572", identifierManager.getNextHivUniquePatientNumber(null));
		Assert.assertEquals("1500100573", identifierManager.getNextHivUniquePatientNumber(null));
		Assert.assertEquals("1500100574", identifierManager.getNextHivUniquePatientNumber(null));
	}*/
}