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
import org.junit.Test;

import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link Metadata}
 */
public class MetadataTest {

	@Test
	public void integration() {
		new Metadata();
		new Metadata.Concept();
		new Metadata.Form();
		new Metadata.IdentifierType();
		new Metadata.Program();

		Assert.assertThat(Metadata.Concept.ABACAVIR, is("70056AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
		Assert.assertThat(Metadata.Form.CLINICAL_ENCOUNTER, is("e958f902-64df-4819-afd4-7fb061f59308"));
		Assert.assertThat(Metadata.IdentifierType.OLD, is("8d79403a-c2cc-11de-8d13-0010c6dffd0f"));
		Assert.assertThat(Metadata.Program.HIV, is("dfdc6d40-2f2f-463d-ba90-cc97350441a8"));
	}
}