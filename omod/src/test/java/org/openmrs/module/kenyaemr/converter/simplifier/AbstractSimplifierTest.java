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
import org.openmrs.ui.framework.SimpleObject;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link AbstractSimplifier}
 */
public class AbstractSimplifierTest {

	/**
	 * @see AbstractSimplifier#convert(Object)
	 */
	@Test
	public void convert_shouldCallSimplify() {
		TestSimplifier simplifier = new TestSimplifier();
		Assert.assertThat(simplifier.convert(123), hasEntry("val", (Object) 123));
	}

	public static final class TestSimplifier extends AbstractSimplifier<Integer> {

		@Override
		protected SimpleObject simplify(Integer obj) {
			return SimpleObject.create("val", obj);
		}
	}
}