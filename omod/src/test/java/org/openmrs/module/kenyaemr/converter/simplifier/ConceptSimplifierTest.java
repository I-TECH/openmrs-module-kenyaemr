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

package org.openmrs.module.kenyaemr.converter.simplifier;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.module.kenyacore.CoreConstants;
import org.openmrs.ui.framework.SimpleObject;

import java.util.Collections;

import static org.hamcrest.Matchers.hasEntry;

/**
 * Tests for {@link ConceptSimplifier}
 */
public class ConceptSimplifierTest {

	private ConceptSimplifier simplifier = new ConceptSimplifier();

	/**
	 * @see ConceptSimplifier#simplify(org.openmrs.Concept)
	 */
	@Test
	public void simplify_shouldSimplify() {
		ConceptName name = new ConceptName();
		name.setName("Test");
		name.setLocale(CoreConstants.LOCALE);
		name.setLocalePreferred(true);

		Concept concept = new Concept();
		concept.setId(123);
		concept.setNames(Collections.singletonList(name));

		SimpleObject result = simplifier.simplify(concept);
		Assert.assertThat(result, hasEntry("id", (Object) 123));
		Assert.assertThat(result, hasEntry("name", (Object) "Test"));
	}
}