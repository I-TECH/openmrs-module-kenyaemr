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

package org.openmrs.module.kenyacore.identifier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyautil.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link org.openmrs.module.kenyacore.identifier.IdentifierManager}
 *
 * Re-enable once moved into new kenyacore module where kenyaemr content won't conflict
 */
@Ignore
public class IdentifierManagerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private IdentifierManager identifierManager;

	@Before
	public void setup() throws Exception {
		identifierManager.refresh();
	}

	/**
	 * @see IdentifierManager#getPatientDisplayIdentifiers(org.openmrs.Patient)
	 */
	@Test
	public void getPatientDisplayIdentifiers() {
		Patient patient = Context.getPatientService().getPatient(7);

		// Void identifier from standard test data
		Context.getPatientService().voidPatientIdentifier(Context.getPatientService().getPatientIdentifier(4), "test");

		// Give patient a single "old identification number"
		PatientIdentifier pidOld = new PatientIdentifier();
		pidOld.setPatient(patient);
		pidOld.setIdentifierType(MetadataUtils.getPatientIdentifierType("2f470aa8-1d73-43b7-81b5-01f0c0dfa53c"));
		pidOld.setIdentifier("1234");
		pidOld.setLocation(Context.getLocationService().getLocation(1)); // Unknown Location
		patient.addIdentifier(pidOld);
		Context.getPatientService().savePatientIdentifier(pidOld);

		// Check identifier is returned in display list
		List<PatientIdentifier> ids = identifierManager.getPatientDisplayIdentifiers(patient);
		Assert.assertThat(ids, containsInAnyOrder(pidOld));

		// Give patient an additional "Openmrs identification number"
		PatientIdentifier pidOpenmrs = new PatientIdentifier();
		pidOpenmrs.setPatient(patient);
		pidOpenmrs.setIdentifierType(MetadataUtils.getPatientIdentifierType("1a339fe9-38bc-4ab3-b180-320988c0b968"));
		pidOpenmrs.setIdentifier("6TS-4");
		pidOpenmrs.setLocation(Context.getLocationService().getLocation(1)); // Unknown Location
		patient.addIdentifier(pidOpenmrs);
		Context.getPatientService().savePatientIdentifier(pidOpenmrs);

		// Check that original old identifier is no longer returned because now it's not the only one
		ids = identifierManager.getPatientDisplayIdentifiers(patient);
		Assert.assertThat(ids, containsInAnyOrder(pidOpenmrs));
	}

	/**
	 * @see org.openmrs.module.kenyacore.identifier.IdentifierManager#getIdentifierSource(org.openmrs.PatientIdentifierType)
	 */
	@Test
	public void getIdentifierSource_shouldReturnNullIfNotSetup() {
		PatientIdentifierType oldIDType = Context.getPatientService().getPatientIdentifierType(2);

		Assert.assertNull(identifierManager.getIdentifierSource(oldIDType));
	}
}