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
import org.openmrs.OrderFrequency;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;

/**
 * Tests for {@link RegimenManager}
 */
public class RegimenManagerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private RegimenManager regimenManager;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-drugs.xml");

		regimenManager.refresh();
	}

	/**
	 * @see RegimenManager#loadDefinitionsFromXML(java.io.InputStream)
	 * @verifies load all definitions
	 */
	@Test
	public void loadDefinitionsFromXML_shouldLoadAllDefinitions() throws Exception {
		Assert.assertThat(regimenManager.getCategoryCodes().size(), greaterThan(0));

		Assert.assertEquals(Dictionary.ANTIRETROVIRAL_DRUGS, regimenManager.getMasterSetConcept("category1").getUuid());

		List<RegimenDefinitionGroup> groups = regimenManager.getRegimenGroups("category1");

		Assert.assertEquals(2, groups.size());
		RegimenDefinitionGroup group1 = groups.get(0);
		RegimenDefinitionGroup group2 = groups.get(1);

		Assert.assertEquals("group1", group1.getCode());
		Assert.assertEquals("Group #1", group1.getName());

		Assert.assertEquals(2, group1.getRegimens().size());
		RegimenDefinition regimen1 = group1.getRegimens().get(0);
		RegimenDefinition regimen2 = group1.getRegimens().get(1);

		Assert.assertEquals("regimen1", regimen1.getName());
		Assert.assertEquals(new Integer(86663), regimen1.getComponents().get(0).getDrugRef().getConcept().getConceptId()); // zidovudine
		Assert.assertEquals(300d, regimen1.getComponents().get(0).getDose(), 0d);
		Assert.assertEquals(new Integer(50), regimen1.getComponents().get(0).getUnits().getConceptId()); // 50 corresponds to mg in test-concepts
		Assert.assertEquals(new Integer(160862), regimen1.getComponents().get(0).getFrequency().getConceptId()); // OD equivalent

		Assert.assertEquals(new Integer(78643), regimen1.getComponents().get(1).getDrugRef().getConcept().getConceptId()); // lamivudine
		Assert.assertEquals(150d, regimen1.getComponents().get(1).getDose(), 0d);
		Assert.assertEquals(new Integer(50), regimen1.getComponents().get(1).getUnits().getConceptId()); // 50 corresponds to mg in test-concepts
		Assert.assertEquals(new Integer(160858), regimen1.getComponents().get(1).getFrequency().getConceptId());

		Assert.assertEquals("regimen2", regimen2.getName());

		Assert.assertEquals("group2", group2.getCode());
		Assert.assertEquals("Group #2", group2.getName());

		Assert.assertEquals(1, group2.getRegimens().size());
		RegimenDefinition regimen3 = group2.getRegimens().get(0);

		Assert.assertEquals("regimen3", regimen3.getName());

		Assert.assertEquals(new Integer(84309), regimen3.getComponents().get(0).getDrugRef().getConcept().getConceptId());
		Assert.assertNull(regimen3.getComponents().get(0).getDose());
		Assert.assertEquals(new Integer(51), regimen3.getComponents().get(0).getUnits().getConceptId()); // 51 corresponds to tabs in test-concepts
		Assert.assertNull(regimen3.getComponents().get(0).getFrequency());
	}

	/**
	 * @see RegimenManager#findDefinitions(String, org.openmrs.module.kenyaemr.regimen.RegimenOrder, boolean)
	 */
	@Test
	public void findDefinitions_shouldFindDefinitionsForRegimen() {
		// Create regimen that matches the regimen2 definition exactly
		OrderFrequency of = new OrderFrequency();
		of.setConcept(Context.getConceptService().getConcept(160862));
		Context.getOrderService().saveOrderFrequency(of);

		OrderFrequency ofBD = new OrderFrequency();
		ofBD.setConcept(Context.getConceptService().getConcept(160858));
		Context.getOrderService().saveOrderFrequency(ofBD);

		DrugOrder lamivudine = new DrugOrder();
		lamivudine.setConcept(Context.getConceptService().getConcept(78643));
		lamivudine.setDose(150d);
		lamivudine.setDoseUnits(Context.getConceptService().getConcept(50));
		lamivudine.setFrequency(ofBD);

		DrugOrder stavudine = new DrugOrder();
		stavudine.setConcept(Context.getConceptService().getConcept(84309));
		stavudine.setDose(30d);
		stavudine.setDoseUnits(Context.getConceptService().getConcept(50));
		stavudine.setFrequency(of);
		RegimenOrder regimen = new RegimenOrder(new HashSet<DrugOrder>(Arrays.asList(lamivudine, stavudine)));

		// Test exact match
		List<RegimenDefinition> defsExact = regimenManager.findDefinitions("category1", regimen, true);
		Assert.assertEquals(1, defsExact.size());
		Assert.assertEquals("regimen2", defsExact.get(0).getName());

		// Test non-exact match
		List<RegimenDefinition> defsNonExact = regimenManager.findDefinitions("category1", regimen, false);
		Assert.assertEquals(2, defsNonExact.size());
		Assert.assertEquals("regimen2", defsNonExact.get(0).getName());
		Assert.assertEquals("regimen3", defsNonExact.get(1).getName());
	}
}