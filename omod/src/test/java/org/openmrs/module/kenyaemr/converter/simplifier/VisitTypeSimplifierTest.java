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