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
import org.openmrs.module.kenyaemr.api.ConfigurationRequiredException;
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
	 * @see org.openmrs.module.kenyaemr.identifier.IdentifierManager#getMrnIdentifierSource()
	 */
	@Test
	public void getMrnIdentifierSource_shouldReturnIdentifierSourceIfSetup() {
		identifierManager.setupMrnIdentifierSource("4");

		Assert.assertNotNull(identifierManager.getMrnIdentifierSource());
	}

	/**
	 * @see org.openmrs.module.kenyaemr.identifier.IdentifierManager#getMrnIdentifierSource()
	 */
	@Test(expected = ConfigurationRequiredException.class)
	public void getMrnIdentifierSource_shouldThrowExceptionIfSourceNotSetup() {
		identifierManager.getMrnIdentifierSource();
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
	 * @see org.openmrs.module.kenyaemr.identifier.IdentifierManager#getHivUniqueIdentifierSource()
	 */
	@Test(expected = ConfigurationRequiredException.class)
	public void getHivUniqueIdentifierSource_shouldThrowExceptionIfSourceNotSetup() {
		identifierManager.getHivUniqueIdentifierSource();
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