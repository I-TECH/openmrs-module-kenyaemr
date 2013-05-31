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

package org.openmrs.module.kenyaemr.lab;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Tests for {@link LabManager}
 */
public class LabManagerTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private LabManager labManager;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("test-data.xml");

		labManager.clear();

		InputStream stream = getClass().getClassLoader().getResourceAsStream("test-lab.xml");
		labManager.loadTestsFromXML(stream);
	}

	/**
	 * @see LabManager#loadTestsFromXML(java.io.InputStream)
	 * @verifies load all tests
	 */
	@Test
	public void loadTestsFromXML_shouldLoadAllTests() throws Exception {
		Assert.assertEquals(2, labManager.getCategories().size());

		List<LabTestDefinition> tests = labManager.getTests("category1");
		Assert.assertEquals(2, tests.size());
		Assert.assertEquals(Dictionary.getConcept(Dictionary.CD4_COUNT), tests.get(0).getConcept());
		Assert.assertEquals(Dictionary.getConcept(Dictionary.CD4_COUNT).getPreferredName(Metadata.LOCALE).getName(), tests.get(0).getName());
		Assert.assertEquals(Dictionary.getConcept(Dictionary.CD4_PERCENT), tests.get(1).getConcept());
		Assert.assertEquals("test-name", tests.get(1).getName());

		tests = labManager.getTests("category2");
		Assert.assertEquals(1, tests.size());
	}

	/**
	 * @see org.openmrs.module.kenyaemr.lab.LabManager#clear()
	 */
	@Test
	public void clear_shouldClearAllTestData() {
		labManager.clear();

		Assert.assertEquals(0, labManager.getCategories().size());
		Assert.assertNull(labManager.getTests("category1"));
	}

	/**
	 * @see org.openmrs.module.kenyaemr.lab.LabManager#isLabTest(org.openmrs.Concept)
	 */
	@Test
	public void isLabTest() {
		Assert.assertTrue(labManager.isLabTest(Dictionary.getConcept(Dictionary.CD4_COUNT)));
		Assert.assertTrue(labManager.isLabTest(Dictionary.getConcept(Dictionary.CD4_PERCENT)));
		Assert.assertFalse(labManager.isLabTest(Dictionary.getConcept(Dictionary.YES)));
		Assert.assertFalse(labManager.isLabTest(null));
	}
}