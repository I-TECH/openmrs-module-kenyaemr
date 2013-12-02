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
import org.springframework.core.convert.converter.Converter;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link StringToEnumConverterFactory}
 */
public class StringToEnumConverterFactoryTest {

	private StringToEnumConverterFactory factory = new StringToEnumConverterFactory();

	/**
	 * @see StringToEnumConverterFactory#getConverter(Class)
	 */
	@Test
	public void getConverter() {
		Converter<String, ? extends Enum> converter = factory.getConverter(TestEnum.class);

		Assert.assertThat(converter.convert(null), nullValue());
		Assert.assertThat(converter.convert(""), nullValue());
		Assert.assertThat(converter.convert("ONE"), is((Object) TestEnum.ONE));
	}

	/**
	 * Enum class for testing
	 */
	public static enum TestEnum {
		ONE,
		TWO,
		THREE
	}
}