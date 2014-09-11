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

package org.openmrs.module.kenyaemr.converter;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link ClassToStringConverter}
 */
public class ClassToStringConverterTest {

	private ClassToStringConverter converter = new ClassToStringConverter();

	/**
	 * @see ClassToStringConverter#convert(Class)
	 */
	@Test
	public void convert_shouldConvertClass() {
		Assert.assertThat(converter.convert(null), is(""));
		Assert.assertThat(converter.convert(this.getClass()), is("org.openmrs.module.kenyaemr.converter.ClassToStringConverterTest"));
	}
}