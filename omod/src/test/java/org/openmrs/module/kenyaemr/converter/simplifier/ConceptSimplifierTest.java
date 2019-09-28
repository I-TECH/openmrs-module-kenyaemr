/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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