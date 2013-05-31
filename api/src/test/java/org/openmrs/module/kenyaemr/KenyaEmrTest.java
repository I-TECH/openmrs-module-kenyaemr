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
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.kenyaemr.form.FormManager;
import org.openmrs.module.kenyaemr.reporting.builder.ReportBuilder;
import org.openmrs.module.kenyaemr.test.TestUtils;
import org.openmrs.module.kenyaemr.util.BuildProperties;
import org.openmrs.module.kenyaemr.util.KenyaEmrUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ModuleFactory.class, Context.class})
public class KenyaEmrTest /*extends BaseModuleContextSensitiveTest*/ {

	KenyaEmr emr = new KenyaEmr();

	/**
	 * @see org.openmrs.module.kenyaemr.KenyaEmr#getModuleVersion()
	 */
	@Test
	public void getModuleVersion() {
		PowerMockito.mockStatic(ModuleFactory.class);

		Module testMod = new Module("Test", "test", "org.openmrs.module", "Bob", "Testing", "1.1");
		Mockito.when(ModuleFactory.getModuleById("kenyaemr")).thenReturn(testMod);

		// Will always return null when run from unit test
		Assert.assertEquals("1.1", emr.getModuleVersion());
	}

	/**
	 * @see org.openmrs.module.kenyaemr.KenyaEmr#getModuleBuildProperties()
	 * @verifies return build properties
	 */
	@Test
	public void getModuleBuildProperties_shouldGetBuildProperties() {
		PowerMockito.mockStatic(Context.class);

		BuildProperties testProps = new BuildProperties();
		testProps.setBuildDate(TestUtils.date(2012, 1, 1));
		testProps.setDeveloper("Test");
		Mockito.when(Context.getRegisteredComponents(BuildProperties.class)).thenReturn(Collections.singletonList(testProps));

		BuildProperties properties = emr.getModuleBuildProperties();

		Assert.assertNotNull(properties);
		Assert.assertNotNull(properties.getBuildDate());
		Assert.assertNotNull(properties.getDeveloper());
	}

	@Test
	public void getSingletonComponent() {
		PowerMockito.mockStatic(Context.class);
		Mockito.when(Context.getRegisteredComponents(Integer.class)).thenReturn(Collections.singletonList(123));

		// Test with class with a registered components
		Assert.assertEquals(new Integer(123), KenyaEmr.getSingletonComponent(Integer.class));

		try {
			// Test with class with no components
			KenyaEmr.getSingletonComponent(Metadata.class);
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