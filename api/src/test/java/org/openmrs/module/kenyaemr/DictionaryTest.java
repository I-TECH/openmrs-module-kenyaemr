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
import org.junit.Before;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests for {@link org.openmrs.module.kenyaemr.Dictionary}
 */
public class DictionaryTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
	}

	@Test
	public void integration() {
		new Dictionary();
	}

	/**
	 * @see Dictionary#getConcept(String)
	 */
	@Test
	public void getConcept_shouldFetchByMappingOrUuid() {
		// Check lookup by UUID
		Assert.assertEquals(Dictionary.CD4_COUNT, Dictionary.getConcept(Dictionary.CD4_COUNT).getUuid());
	}

	/**
	 * @see Dictionary#getConcept(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getConcept_shouldThrowExceptionForNonExistent() {
		Dictionary.getConcept("PIH:XXXXXXXXXXXXXXX");
	}
}