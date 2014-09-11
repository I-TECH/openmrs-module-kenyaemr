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
import org.openmrs.VisitType;
import org.openmrs.ui.framework.SimpleObject;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link VisitTypeSimplifier}
 */
public class VisitTypeSimplifierTest {

	private VisitTypeSimplifier simplifier = new VisitTypeSimplifier();

	/**
	 * @see VisitTypeSimplifier#simplify(org.openmrs.VisitType)
	 */
	@Test
	public void simplify_shouldSimplify() {
		VisitType visitType = new VisitType();
		visitType.setId(123);
		visitType.setName("test");
		visitType.setDescription("desc");

		SimpleObject result = simplifier.simplify(visitType);
		Assert.assertThat(result, hasEntry("id", (Object) 123));
		Assert.assertThat(result, hasEntry("name", (Object) "test"));
		Assert.assertThat(result, hasEntry("description", (Object) "desc"));
	}
}