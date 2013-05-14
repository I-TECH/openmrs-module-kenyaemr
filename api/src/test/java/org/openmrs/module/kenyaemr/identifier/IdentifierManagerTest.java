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
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

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

		emr.getMetadataManager().setupGlobalProperties();
	}

	/**
	 * @see IdentifierManager#setupMrnIdentifierSource(String)
	 * @verifies set up an identifier source
	 */
	@Test
	public void setupMrnIdentifierSource_shouldSetUpAnIdentifierSource() throws Exception {
		Assert.assertFalse(isMrnIdentifierSourceSetup());
		identifierManager.setupMrnIdentifierSource("4");
		Assert.assertTrue(isMrnIdentifierSourceSetup());
		IdentifierSource source = identifierManager.getMrnIdentifierSource();
		Assert.assertNotNull(source);

		PatientIdentifierType idType = source.getIdentifierType();
		Assert.assertEquals("M4E", Context.getService(IdentifierSourceService.class).generateIdentifier(idType, "Testing"));
		Assert.assertEquals("M6C", Context.getService(IdentifierSourceService.class).generateIdentifier(idType, "Testing"));
		Assert.assertEquals("M79", Context.getService(IdentifierSourceService.class).generateIdentifier(idType, "Testing"));
	}

	/**
	 * @return whether the MRN identifier source has been set up
	 */
	private boolean isMrnIdentifierSourceSetup() {
		try {
			IdentifierSource source = identifierManager.getMrnIdentifierSource();
			return source != null;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * @see IdentifierManager#setupMrnIdentifierSource(String)
	 * @verifies fail if already set up
	 */
	@Test
	public void setupMrnIdentifierSource_shouldFailIfAlreadySetUp() throws Exception {
		identifierManager.setupMrnIdentifierSource("4");
		try {
			identifierManager.setupMrnIdentifierSource("4");
			Assert.fail("Shouldn't be allowed to set up twice");
		} catch (Exception ex) {
			// pass
		}
	}

	/**
	 * @see IdentifierManager#setupHivUniqueIdentifierSource(String)
	 * @verifies fail if already set up
	 */
	@Test
	public void setupHivUniqueIdentifierSource_shouldFailIfAlreadySetUp() throws Exception {
		identifierManager.setupHivUniqueIdentifierSource("00517");
		try {
			identifierManager.setupHivUniqueIdentifierSource("00517");
			Assert.fail("Shouldn't be allowed to set up twice");
		} catch (Exception ex) {
			// pass
		}
	}

	/**
	 * @see IdentifierManager#setupHivUniqueIdentifierSource(String)
	 * @verifies set up an identifier source
	 */
	@Test
	public void setupHivUniqueIdentifierSource_shouldSetUpAnIdentifierSource() throws Exception {
		Assert.assertFalse(isHivIdentifierSourceSetup());
		identifierManager.setupHivUniqueIdentifierSource("00517");
		Assert.assertTrue(isHivIdentifierSourceSetup());
		IdentifierSource source = identifierManager.getHivUniqueIdentifierSource();
		Assert.assertNotNull(source);

		PatientIdentifierType idType = source.getIdentifierType();
		Assert.assertEquals("00517", Context.getService(IdentifierSourceService.class).generateIdentifier(idType, "Testing"));
		Assert.assertEquals("00518", Context.getService(IdentifierSourceService.class).generateIdentifier(idType, "Testing"));
		Assert.assertEquals("00519", Context.getService(IdentifierSourceService.class).generateIdentifier(idType, "Testing"));
	}

	/**
	 * @return whether the HIV identifier source has been set up
	 */
	private boolean isHivIdentifierSourceSetup() {
		try {
			IdentifierSource source = identifierManager.getHivUniqueIdentifierSource();
			return source != null;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * @see IdentifierManager#getNextHivUniquePatientNumber(String)
	 * @verifies get sequential numbers with mfl prefix
	 */
	@Test
	public void getNextHivUniquePatientNumber_shouldGetSequentialNumbersWithMflPrefix() throws Exception {
		Location loc = Context.getLocationService().getLocation(1);
		Assert.assertNotNull(loc);
		Context.getService(KenyaEmrService.class).setDefaultLocation(loc);

		identifierManager.setupHivUniqueIdentifierSource("00571");
		Assert.assertEquals("1500100571", identifierManager.getNextHivUniquePatientNumber(null));
		Assert.assertEquals("1500100572", identifierManager.getNextHivUniquePatientNumber(null));
		Assert.assertEquals("1500100573", identifierManager.getNextHivUniquePatientNumber(null));
		Assert.assertEquals("1500100574", identifierManager.getNextHivUniquePatientNumber(null));
	}
}