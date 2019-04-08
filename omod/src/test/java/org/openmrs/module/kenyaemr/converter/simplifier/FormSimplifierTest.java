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