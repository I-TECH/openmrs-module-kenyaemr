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

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.regimen.DrugReference;
import org.openmrs.module.kenyaemr.converter.StringToDrugReferenceConverter;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

/**
 *
 */
public class StringToDrugReferenceConverterTest extends BaseModuleWebContextSensitiveTest {

	private StringToDrugReferenceConverter converter = new StringToDrugReferenceConverter();

	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");
		executeDataSet("test-drugdata.xml");
	}

	/**
	 * @see org.openmrs.module.kenyaemr.converter.StringToRegimenConverter#convert(String)
	 */
	@Test
	public void convert_shouldConvertString() {
		// Test concept only reference
		DrugReference drugRef1 = converter.convert("C$84309AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		Assert.assertTrue(drugRef1.isConceptOnly());
		Assert.assertEquals(Context.getConceptService().getConceptByUuid("84309AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), drugRef1.getConcept());

		// Test drug object reference
		DrugReference drugRef2 = converter.convert("D$71617-drug");
		Assert.assertFalse(drugRef2.isConceptOnly());
		Assert.assertEquals(Context.getConceptService().getDrugByUuid("71617-drug"), drugRef2.getDrug());

		// Test invalid reference
		DrugReference drugRef3 = converter.convert("xxxx");
		Assert.assertNull(drugRef3);
	}
}