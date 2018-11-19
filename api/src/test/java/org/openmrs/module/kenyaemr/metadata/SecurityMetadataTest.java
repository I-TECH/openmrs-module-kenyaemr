/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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