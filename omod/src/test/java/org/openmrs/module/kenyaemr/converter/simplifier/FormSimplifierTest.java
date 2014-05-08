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
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Form;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasEntry;

/**
 * Tests for {@link FormSimplifier}
 */
public class FormSimplifierTest extends BaseModuleWebContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private FormSimplifier simplifier;

	@Before
	public void setup() {
		commonMetadata.install();
	}

	/**
	 * @see FormSimplifier#simplify(org.openmrs.Form)
	 */
	@Test
	public void simplify_shouldSimplify() {
		Form form = MetadataUtils.existing(Form.class, CommonMetadata._Form.TRIAGE);

		SimpleObject result = simplifier.simplify(form);
		Assert.assertThat(result, hasEntry("id", (Object) form.getId()));
		Assert.assertThat(result, hasEntry("name", (Object) form.getName()));
		Assert.assertThat(result, hasEntry("uuid", (Object) form.getUuid()));
	}
}