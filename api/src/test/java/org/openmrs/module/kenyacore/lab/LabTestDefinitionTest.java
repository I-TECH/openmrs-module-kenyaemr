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

package org.openmrs.module.kenyacore.lab;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.CoreConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Tests for {@link LabTestDefinitionTest}
 */
public class LabTestDefinitionTest extends BaseModuleContextSensitiveTest {

	@Test
	public void integration() {
		final String cd4Uuid = "a09ab2c5-878e-4905-b25d-5784167d0216";
		final Concept cd4 = Context.getConceptService().getConceptByUuid(cd4Uuid);

		// Check with concept uuid
		LabTestDefinition def = new LabTestDefinition(cd4Uuid);
		Assert.assertEquals(cd4, def.getConcept());
		Assert.assertEquals(cd4.getPreferredName(CoreConstants.LOCALE).getName(), def.getName());

		// Check with concept uuid and name
		def = new LabTestDefinition(cd4Uuid, "test-name");
		Assert.assertEquals(cd4, def.getConcept());
		Assert.assertEquals("test-name", def.getName());
	}
}