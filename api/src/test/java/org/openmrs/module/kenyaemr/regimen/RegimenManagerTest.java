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

package org.openmrs.module.kenyaemr.regimen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.*;

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
		Assert.assertEquals("mg", regimen1.getComponents().get(0).getUnits());
		Assert.assertEquals("OD", regimen1.getComponents().get(0).getFrequency());

		Assert.assertEquals(new Integer(78643), regimen1.getComponents().get(1).getDrugRef().getConcept().getConceptId()); // lamivudine
		Assert.assertEquals(150d, regimen1.getComponents().get(1).getDose(), 0d);
		Assert.assertEquals("mg", regimen1.getComponents().get(1).getUnits());
		Assert.assertEquals("BD", regimen1.getComponents().get(1).getFrequency());

		Assert.assertEquals("regimen2", regimen2.getName());

		Assert.assertEquals("group2", group2.getCode());
		Assert.assertEquals("Group #2", group2.getName());

		Assert.assertEquals(1, group2.getRegimens().size());
		RegimenDefinition regimen3 = group2.getRegimens().get(0);

		Assert.assertEquals("regimen3", regimen3.getName());

		Assert.assertEquals(new Integer(84309), regimen3.getComponents().get(0).getDrugRef().getConcept().getConceptId());
		Assert.assertNull(regimen3.getComponents().get(0).getDose());
		Assert.assertEquals("tab", regimen3.getComponents().get(0).getUnits());
		Assert.assertNull(regimen3.getComponents().get(0).getFrequency());
	}

	/**
	 * @see RegimenManager#findDefinitions(String, org.openmrs.module.kenyaemr.regimen.RegimenOrder, boolean)
	 */
	@Test
	public void findDefinitions_shouldFindDefinitionsForRegimen() {
		// Create regimen that matches the regimen2 definition exactly
		DrugOrder lamivudine = new DrugOrder();
		lamivudine.setConcept(Context.getConceptService().getConcept(78643));
		lamivudine.setDose(150d);
		lamivudine.setUnits("mg");
		lamivudine.setFrequency("BD");
		DrugOrder stavudine = new DrugOrder();
		stavudine.setConcept(Context.getConceptService().getConcept(84309));
		stavudine.setDose(30d);
		stavudine.setUnits("mg");
		stavudine.setFrequency("OD");
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