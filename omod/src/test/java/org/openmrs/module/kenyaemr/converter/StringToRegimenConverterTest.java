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
import org.openmrs.module.kenyaemr.regimen.Regimen;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

/**
 * Tests for {@link StringToRegimenConverter}
 */
public class StringToRegimenConverterTest extends BaseModuleWebContextSensitiveTest {

	private StringToRegimenConverter converter = new StringToRegimenConverter();

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-drugs.xml");
	}

	/**
	 * @see StringToRegimenConverter#convert(String)
	 */
	@Test
	public void convert_shouldConvertString() {
		// Test single component regimen
		Regimen regimen1 = converter.convert("C$84309AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA|300|mg|OD");

		Assert.assertEquals(1, regimen1.getComponents().size());
		Assert.assertEquals(new Integer(84309), regimen1.getComponents().get(0).getDrugRef().getConcept().getConceptId());
		Assert.assertEquals(new Double(300.0), regimen1.getComponents().get(0).getDose());
		Assert.assertEquals("mg", regimen1.getComponents().get(0).getUnits());
		Assert.assertEquals("OD", regimen1.getComponents().get(0).getFrequency());

		// Test multiple component regimen
		Regimen regimen2 = converter.convert("C$84309AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA|300|mg|OD|D$97810e6b-cfcf-44fa-b63c-5d3e12cbe8d7|150|ml|BD");

		Assert.assertEquals(2, regimen2.getComponents().size());
		Assert.assertEquals(new Integer(84309), regimen2.getComponents().get(0).getDrugRef().getConcept().getConceptId());
		Assert.assertEquals(new Double(300.0), regimen2.getComponents().get(0).getDose());
		Assert.assertEquals("mg", regimen2.getComponents().get(0).getUnits());
		Assert.assertEquals("OD", regimen2.getComponents().get(0).getFrequency());
		Assert.assertEquals(new Integer(200004), regimen2.getComponents().get(1).getDrugRef().getDrug().getDrugId());
		Assert.assertEquals(new Double(150.0), regimen2.getComponents().get(1).getDose());
		Assert.assertEquals("ml", regimen2.getComponents().get(1).getUnits());
		Assert.assertEquals("BD", regimen2.getComponents().get(1).getFrequency());

		// Test empty component properties
		Regimen regimen3 = converter.convert("C$84309AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA||mg|");

		Assert.assertEquals(1, regimen3.getComponents().size());
		Assert.assertEquals(new Integer(84309), regimen3.getComponents().get(0).getDrugRef().getConcept().getConceptId());
		Assert.assertNull(regimen3.getComponents().get(0).getDose());
		Assert.assertEquals("mg", regimen3.getComponents().get(0).getUnits());
		Assert.assertNull(regimen3.getComponents().get(0).getFrequency());

		// Test blank component properties
		Regimen regimen4 = converter.convert("C$84309AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA| |mg| ");

		Assert.assertEquals(1, regimen4.getComponents().size());
		Assert.assertEquals(new Integer(84309), regimen4.getComponents().get(0).getDrugRef().getConcept().getConceptId());
		Assert.assertNull(regimen4.getComponents().get(0).getDose());
		Assert.assertEquals("mg", regimen4.getComponents().get(0).getUnits());
		Assert.assertNull(regimen4.getComponents().get(0).getFrequency());
	}
}