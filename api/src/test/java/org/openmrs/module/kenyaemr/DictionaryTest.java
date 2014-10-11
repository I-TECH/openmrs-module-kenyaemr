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

package org.openmrs.module.kenyaemr;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.context.Context;
import org.openmrs.module.metadatadeploy.MissingMetadataException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link Dictionary}
 */
public class DictionaryTest extends BaseModuleContextSensitiveTest {

	/**
	 * Setup each test
	 */
	@Before
	public void setup() throws Exception {
		executeDataSet("dataset/test-concepts.xml");
	}

	@Test
	public void integration() {
		new Dictionary();
	}

	/**
	 * @see Dictionary#getConcept(String)
	 */
	@Test
	public void getConcept_shouldFetchByMappingOrUuid() {
		// Check lookup by UUID
		Concept cd4 = Context.getConceptService().getConceptByUuid(Dictionary.CD4_COUNT);
		Concept fetched = Dictionary.getConcept(Dictionary.CD4_COUNT);
		Assert.assertThat(fetched, is(cd4));
		Assert.assertThat(fetched, is(instanceOf(ConceptNumeric.class)));
	}

	/**
	 * @see Dictionary#getConcept(String)
	 */
	@Test(expected = MissingMetadataException.class)
	public void getConcept_shouldThrowExceptionForNonExistent() {
		Dictionary.getConcept("PIH:XXXXXXXXXXXXXXX");
	}

	/**
	 * @see Dictionary#getConcepts(String...)
	 */
	@Test
	public void getConcepts_shouldFetchByMappingOrUuid() {
		// Check lookup by UUID
		Concept cd4 = Context.getConceptService().getConceptByUuid(Dictionary.CD4_COUNT);
		Concept cd4pc = Context.getConceptService().getConceptByUuid(Dictionary.CD4_PERCENT);
		Assert.assertThat(Dictionary.getConcepts(Dictionary.CD4_COUNT, Dictionary.CD4_PERCENT), contains(cd4, cd4pc));
	}

	/**
	 * @see Dictionary#getConcepts(String...)
	 */
	@Test(expected = MissingMetadataException.class)
	public void getConcepts_shouldThrowExceptionForNonExistent() {
		Dictionary.getConcepts(Dictionary.CD4_COUNT, "PIH:XXXXXXXXXXXXXXX");
	}
}