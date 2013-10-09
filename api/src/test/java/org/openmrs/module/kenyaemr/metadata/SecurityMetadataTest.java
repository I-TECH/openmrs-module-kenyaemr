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

package org.openmrs.module.kenyaemr.metadata;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link SecurityMetadata}
 */
public class SecurityMetadataTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private SecurityMetadata securityMetadata;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		TestUtils.savePrivilege("View Patients", "Testing");
		TestUtils.savePrivilege("Edit Orders", "Testing");
		TestUtils.savePrivilege("Delete Users", "Testing");
	}

	/**
	 * @see SecurityMetadata#install()
	 */
	@Test
	public void install_shouldInstallAllMetadata() {
		securityMetadata.install();

		Assert.assertNotNull(Context.getUserService().getRole("Registration"));
	}

	/**
	 * @see SecurityMetadata#getApiPrivileges(boolean)
	 */
	@Test
	public void getApiPrivileges_shouldGetAllApiPrivileges() {
		Set<String> all = securityMetadata.getApiPrivileges(true);

		Assert.assertTrue(all.contains("View Patients"));
		Assert.assertTrue(all.contains("Edit Orders"));
		Assert.assertTrue(all.contains("Delete Users"));

		Set<String> nonDeletes = securityMetadata.getApiPrivileges(false);

		Assert.assertTrue(nonDeletes.contains("View Patients"));
		Assert.assertTrue(nonDeletes.contains("Edit Orders"));
		Assert.assertFalse(nonDeletes.contains("Delete Users"));
	}
}