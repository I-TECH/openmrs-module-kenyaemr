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

package org.openmrs.module.kenyaemr.lab;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests for {@link LabTestDefinitionTest}
 */
public class LabTestDefinitionTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
	}

	@Test
	public void integration() {
		// Check with concept uuid
		LabTestDefinition def = new LabTestDefinition(Dictionary.CD4_COUNT);
		Assert.assertEquals(Dictionary.getConcept(Dictionary.CD4_COUNT), def.getConcept());
		Assert.assertEquals(Dictionary.getConcept(Dictionary.CD4_COUNT).getPreferredName(MetadataConstants.LOCALE).getName(), def.getName());

		// Check with concept uuid and name
		def = new LabTestDefinition(Dictionary.CD4_COUNT, "test-name");
		Assert.assertEquals(Dictionary.getConcept(Dictionary.CD4_COUNT), def.getConcept());
		Assert.assertEquals("test-name", def.getName());
	}
}