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
import org.junit.Test;

import static org.hamcrest.Matchers.*;

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