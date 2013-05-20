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
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

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
	public void getConcept_shouldFetchConceptByMappingOrUuid() {
		// Check lookup by UUID
		Assert.assertEquals(Dictionary.CD4_COUNT, Dictionary.getConcept(Dictionary.CD4_COUNT).getUuid());

		try {
			// Check that valid uuid with no concept throws exception
			Dictionary.getConcept("79BC2641-C169-4780-BBA9-796DEAE9055D");
			Assert.fail();
		} catch (Exception ex) {}

		try {
			// Check that valid mapping with no concept throws exception
			Dictionary.getConcept("PIH:XXXXXXXXXXXXXXX");
			Assert.fail();
		} catch (Exception ex) {}
	}
}