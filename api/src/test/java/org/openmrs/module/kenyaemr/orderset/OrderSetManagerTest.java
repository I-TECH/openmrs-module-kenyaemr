/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.orderset;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.regimen.RegimenDefinition;
import org.openmrs.module.kenyaemr.regimen.RegimenDefinitionGroup;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.greaterThan;

/**
 * Tests for {@link RegimenManager}
 */
public class OrderSetManagerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private OrderSetManager orderSetManager;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
		executeDataSet("dataset/test-drugs.xml");

		orderSetManager.refresh();
	}

	/**
	 * @see RegimenManager#loadDefinitionsFromXML(java.io.InputStream)
	 * @verifies load all definitions
	 */
	@Test
	public void loadDefinitionsFromXML_shouldLoadAllDefinitions() throws Exception {
		Assert.assertThat(orderSetManager.getCategoryCodes().size(), greaterThan(0));

		Assert.assertEquals(Dictionary.ANTIRETROVIRAL_DRUGS, orderSetManager.getMasterSetConcept("category1").getUuid());

		List<RegimenDefinitionGroup> groups = orderSetManager.getRegimenGroups("category1");

		Assert.assertEquals(2, groups.size());
		RegimenDefinitionGroup group1 = groups.get(0);
		RegimenDefinitionGroup group2 = groups.get(1);

		Assert.assertEquals("group1", group1.getCode());
		Assert.assertEquals("Group #1", group1.getName());

		Assert.assertEquals(2, group1.getRegimens().size());

		orderSetManager.populateOrderSets();
		Assert.assertTrue(true);


	}


}