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
import org.openmrs.module.kenyaemr.form.FormManager;
import org.openmrs.module.kenyaemr.reporting.builder.ReportBuilder;
import org.openmrs.module.kenyaemr.util.BuildProperties;
import org.openmrs.module.kenyaemr.util.KenyaEmrUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class KenyaEmrTest extends BaseModuleContextSensitiveTest {

	@Autowired
	KenyaEmr emr;

	/**
	 * @see org.openmrs.module.kenyaemr.KenyaEmr#getModuleBuildProperties()
	 * @verifies return build properties
	 */
	@Test
	public void getModuleBuildProperties_shouldGetBuildProperties() {
		BuildProperties properties = emr.getModuleBuildProperties();

		Assert.assertNotNull(properties);
		Assert.assertNotNull(properties.getBuildDate());
		Assert.assertNotNull(properties.getDeveloper());
	}

	@Test
	public void getSingletonComponent() {
		// Test with known singletons
		Assert.assertNotNull(KenyaEmr.getSingletonComponent(FormManager.class));
		Assert.assertNotNull(KenyaEmr.getSingletonComponent(BuildProperties.class));

		try {
			// Test with class known to have no components
			KenyaEmr.getSingletonComponent(MetadataConstants.class);
			Assert.fail();
		}
		catch (Exception ex) {
		}

		/*try { Re-enable this when TRUNK-3889 is fixed
			// Test with class known to have more than one component
			KenyaEmr.getSingletonComponent(ReportBuilder.class);
			Assert.fail();
		}
		catch (Exception ex) {
		}*/
	}
}