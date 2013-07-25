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

package org.openmrs.module.kenyacore.program;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link org.openmrs.module.kenyacore.program.ProgramManager}
 */
public class ProgramManagerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private ProgramManager programManager;

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");

		programManager.refresh();
	}

	@Test
	public void getAllPrograms() {
		Assert.assertNotNull(programManager.getAllProgramDescriptors());
	}
}