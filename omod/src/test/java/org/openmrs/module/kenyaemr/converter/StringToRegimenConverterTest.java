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
import org.junit.Ignore;
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
		Assert.assertEquals(new Integer(50), regimen1.getComponents().get(0).getUnits().getConceptId()); // 50 corresponds to mg in test-concepts
		Assert.assertEquals(new Integer(160862), regimen1.getComponents().get(0).getFrequency().getConceptId()); // OD equivalent

		// Test multiple component regimen
		Regimen regimen2 = converter.convert("C$84309AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA|300|mg|OD|D$97810e6b-cfcf-44fa-b63c-5d3e12cbe8d7|150|ml|BD");

		Assert.assertEquals(2, regimen2.getComponents().size());
		Assert.assertEquals(new Integer(84309), regimen2.getComponents().get(0).getDrugRef().getConcept().getConceptId());
		Assert.assertEquals(new Double(300.0), regimen2.getComponents().get(0).getDose());
		Assert.assertEquals(new Integer(50), regimen2.getComponents().get(0).getUnits().getConceptId()); // 50 corresponds to mg in test-concepts
		Assert.assertEquals(new Integer(160862), regimen2.getComponents().get(0).getFrequency().getConceptId()); // OD equivalent

		Assert.assertEquals(new Integer(200004), regimen2.getComponents().get(1).getDrugRef().getDrug().getDrugId());
		Assert.assertEquals(new Double(150.0), regimen2.getComponents().get(1).getDose());
		Assert.assertEquals(new Integer(160858), regimen2.getComponents().get(1).getFrequency().getConceptId()); // OD equivalent


		// Test empty component properties
		Regimen regimen3 = converter.convert("C$84309AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA||mg|");

		Assert.assertEquals(1, regimen3.getComponents().size());
		Assert.assertEquals(new Integer(84309), regimen3.getComponents().get(0).getDrugRef().getConcept().getConceptId());
		Assert.assertNull(regimen3.getComponents().get(0).getDose());
		Assert.assertEquals(new Integer(50), regimen3.getComponents().get(0).getUnits().getConceptId()); // 50 corresponds to mg in test-concepts
		Assert.assertNull(regimen3.getComponents().get(0).getFrequency());

		// Test blank component properties
		Regimen regimen4 = converter.convert("C$84309AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA| |mg| ");

		Assert.assertEquals(1, regimen4.getComponents().size());
		Assert.assertEquals(new Integer(84309), regimen4.getComponents().get(0).getDrugRef().getConcept().getConceptId());
		Assert.assertNull(regimen4.getComponents().get(0).getDose());
		Assert.assertEquals(new Integer(50), regimen4.getComponents().get(0).getUnits().getConceptId()); // 50 corresponds to mg in test-concepts
		Assert.assertNull(regimen4.getComponents().get(0).getFrequency());
	}
}