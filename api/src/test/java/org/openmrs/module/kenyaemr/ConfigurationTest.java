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
import org.openmrs.api.AdministrationService;
import org.openmrs.module.kenyaemr.visit.EmrVisitAssignmentHandler;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link Configuration}
 */
public class ConfigurationTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private AdministrationService adminService;

	@Test
	public void integration() {
		new Configuration();
	}

	@Test
	public void configure() {
		Assert.assertThat(adminService.getGlobalProperty(OpenmrsConstants.GP_VISIT_ASSIGNMENT_HANDLER), nullValue());

		Configuration.configure();

		Assert.assertThat(adminService.getGlobalProperty(OpenmrsConstants.GP_VISIT_ASSIGNMENT_HANDLER), is(EmrVisitAssignmentHandler.class.getName()));
	}
}