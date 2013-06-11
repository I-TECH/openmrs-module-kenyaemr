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
import org.junit.Test;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.metadata.MetadataManager;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.metadata.MetadataManager}
 */
public class MetadataManagerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private MetadataManager metadataManager;

	/**
	 * @see org.openmrs.module.kenyaemr.metadata.MetadataManager#installMetadataPackageIfNecessary(String, String, ClassLoader)
	 */
	@Test
	public void installMetadataPackageIfNecessary_shouldInstallPackages() throws Exception {

		final String PACKAGE_GROUP_UUID = "5c7fd8e7-e9a5-43a2-8ba5-c7694fc8db4a";
		final String PACKAGE_FILENAME = "test-package-1.zip";

		try {
			// Check data isn't there
			Metadata.getVisitType(Metadata.OUTPATIENT_VISIT_TYPE);
			Assert.fail();
		}
		catch (IllegalArgumentException ex) {
		}

		// Simulate first time startup
		Assert.assertTrue(metadataManager.installMetadataPackageIfNecessary(PACKAGE_GROUP_UUID, PACKAGE_FILENAME, null));
		Assert.assertNotNull(Metadata.getVisitType(Metadata.OUTPATIENT_VISIT_TYPE));

		// Simulate starting a second time
		Assert.assertFalse(metadataManager.installMetadataPackageIfNecessary(PACKAGE_GROUP_UUID, PACKAGE_FILENAME, null));
		Assert.assertNotNull(Metadata.getVisitType(Metadata.OUTPATIENT_VISIT_TYPE));
	}
}