/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

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