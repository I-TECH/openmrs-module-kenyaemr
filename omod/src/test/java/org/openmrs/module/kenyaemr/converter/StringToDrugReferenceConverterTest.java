/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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