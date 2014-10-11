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

package org.openmrs.module.kenyaemr.converter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.regimen.DrugReference;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

/**
 * Tests for {@link StringToDrugReferenceConverter}
 */
public class StringToDrugReferenceConverterTest extends BaseModuleWebContextSensitiveTest {

	private StringToDrugReferenceConverter converter = new StringToDrugReferenceConverter();

	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-drugs.xml");
	}

	/**
	 * @see StringToDrugReferenceConverter#convert(String)
	 */
	@Test
	public void convert_shouldConvertString() {
		// Test concept only reference
		DrugReference drugRef1 = converter.convert("C$84309AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		Assert.assertTrue(drugRef1.isConceptOnly());
		Assert.assertEquals(Context.getConceptService().getConceptByUuid("84309AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), drugRef1.getConcept());

		// Test drug object reference
		DrugReference drugRef2 = converter.convert("D$97810e6b-cfcf-44fa-b63c-5d3e12cbe8d7");
		Assert.assertFalse(drugRef2.isConceptOnly());
		Assert.assertEquals(Context.getConceptService().getDrugByUuid("97810e6b-cfcf-44fa-b63c-5d3e12cbe8d7"), drugRef2.getDrug());

		// Test invalid references
		Assert.assertNull(converter.convert("xxxx")); // No $ divider
		Assert.assertNull(converter.convert("B$123")); // Divider isn't C or D
	}
}