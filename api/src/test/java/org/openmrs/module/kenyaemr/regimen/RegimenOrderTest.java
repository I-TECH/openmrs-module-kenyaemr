/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.regimen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Tests for {@link RegimenOrder}
 */
public class RegimenOrderTest extends BaseModuleContextSensitiveTest {

	private RegimenOrder regimen1, regimen2, regimen3, regimen4;

	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-drugs.xml");

		DrugOrder lamivudine = new DrugOrder();
		lamivudine.setConcept(Context.getConceptService().getConcept(78643));
		lamivudine.setDose(150d);
		//lamivudine.setUnits("mg");
		//lamivudine.setFrequency("BD");

		DrugOrder stavudine1 = new DrugOrder();
		stavudine1.setConcept(Context.getConceptService().getConcept(84309));
		stavudine1.setDose(30d);
		//stavudine1.setUnits("mg");
		//stavudine1.setFrequency("OD");

		DrugOrder stavudine2 = new DrugOrder();
		stavudine2.setConcept(Context.getConceptService().getConcept(84309));
		stavudine2.setDose(30d);
		//stavudine2.setUnits("mg");
		//stavudine2.setFrequency("OD");

		regimen1 = new RegimenOrder(new LinkedHashSet<DrugOrder>(Arrays.asList(stavudine1, lamivudine)));
		regimen2 = new RegimenOrder(new LinkedHashSet<DrugOrder>(Arrays.asList(lamivudine, stavudine1)));
		regimen3 = new RegimenOrder(new LinkedHashSet<DrugOrder>(Arrays.asList(lamivudine)));
		regimen4 = new RegimenOrder(new LinkedHashSet<DrugOrder>(Arrays.asList(stavudine2, lamivudine)));
	}

	/**
	 * @see RegimenOrder#equals(Object)
	 */
	@Test
	public void equals_shouldCompareDrugOrderExactly() {
		// Same drug orders, different order
		Assert.assertTrue(regimen1.equals(regimen2));
		Assert.assertTrue(regimen2.equals(regimen1));

		// Overlapping but different drugs orders
		Assert.assertFalse(regimen1.equals(regimen3));
		Assert.assertFalse(regimen3.equals(regimen1));

		// Drug order equal, but not the same
		Assert.assertFalse(regimen1.equals(regimen4));
		Assert.assertFalse(regimen4.equals(regimen1));

		// Null regimen
		Assert.assertFalse(regimen1.equals(null));
	}

	/**
	 * @see RegimenOrder#equals(Object)
	 */
	@Test
	public void hasSameDrugs_shouldCompareDrugsOnly() {
		// Same drug orders, different order
		Assert.assertTrue(regimen1.hasSameDrugs(regimen2));
		Assert.assertTrue(regimen2.hasSameDrugs(regimen1));

		// Overlapping but different drugs orders
		Assert.assertFalse(regimen1.hasSameDrugs(regimen3));
		Assert.assertFalse(regimen3.hasSameDrugs(regimen1));

		// Drug order equal, but not the same
		Assert.assertTrue(regimen1.hasSameDrugs(regimen4));
		Assert.assertTrue(regimen4.hasSameDrugs(regimen1));

		// Null regimen
		Assert.assertFalse(regimen1.hasSameDrugs(null));
	}
}