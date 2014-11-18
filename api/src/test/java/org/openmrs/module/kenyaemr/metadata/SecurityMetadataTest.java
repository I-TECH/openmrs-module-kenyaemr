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
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.test.TestUtils;
import org.openmrs.module.metadatadeploy.MetadataUtils;
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

		Context.flushSession();
		Context.clearSession();

		Role apiPrivilegesViewAndEdit = MetadataUtils.existing(Role.class, "API Privileges (View and Edit)");
		Role registration = MetadataUtils.existing(Role.class, "Registration");

		Assert.assertThat(registration.getInheritedRoles(), contains(apiPrivilegesViewAndEdit));
		Assert.assertThat(registration.hasPrivilege("App: kenyaemr.registration"), is(true));
		Assert.assertThat(registration.hasPrivilege("App: kenyaemr.directory"), is(true));
		Assert.assertThat(registration.hasPrivilege("App: kenyaemr.facilities"), is(true));
		Assert.assertThat(registration.hasPrivilege("App: kenyaemr.admin"), is(false));
	}

	/**
	 * @see SecurityMetadata#getApiPrivileges(boolean)
	 */
	@Test
	public void getApiPrivileges_shouldGetAllApiPrivileges() {
		Set<String> all = securityMetadata.getApiPrivileges(true);

		Assert.assertThat(all.contains("View Patients"), is(true));
		Assert.assertThat(all.contains("Edit Orders"), is(true));
		Assert.assertThat(all.contains("Delete Users"), is(true));

		Set<String> nonDeletes = securityMetadata.getApiPrivileges(false);

		Assert.assertThat(nonDeletes.contains("View Patients"), is(true));
		Assert.assertThat(nonDeletes.contains("Edit Orders"), is(true));
		Assert.assertThat(nonDeletes.contains("Delete Users"), is(false));
	}
}