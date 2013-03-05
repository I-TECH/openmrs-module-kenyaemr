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
package org.openmrs.module.kenyaemr;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class MetadataManagerTest extends BaseModuleContextSensitiveTest {

	/**
	 * @see org.openmrs.module.kenyaemr.MetadataManager#installMetadataPackageIfNecessary(String, String, ClassLoader)
	 */
	@Test
	public void installMetadataPackageIfNecessary_shouldInstallPackages() throws Exception {

		final String PACKAGE_GROUP_UUID = "5c7fd8e7-e9a5-43a2-8ba5-c7694fc8db4a";
		final String PACKAGE_FILENAME = "test-package-1.zip";

		// Simulate first time startup
		Assert.assertNull(Context.getVisitService().getVisitTypeByUuid(MetadataConstants.OUTPATIENT_VISIT_TYPE_UUID));
		Assert.assertTrue(MetadataManager.installMetadataPackageIfNecessary(PACKAGE_GROUP_UUID, PACKAGE_FILENAME, null));
		Assert.assertNotNull(Context.getVisitService().getVisitTypeByUuid(MetadataConstants.OUTPATIENT_VISIT_TYPE_UUID));

		// Simulate starting a second time
		Assert.assertFalse(MetadataManager.installMetadataPackageIfNecessary(PACKAGE_GROUP_UUID, PACKAGE_FILENAME, null));
		Assert.assertNotNull(Context.getVisitService().getVisitTypeByUuid(MetadataConstants.OUTPATIENT_VISIT_TYPE_UUID));
	}
}