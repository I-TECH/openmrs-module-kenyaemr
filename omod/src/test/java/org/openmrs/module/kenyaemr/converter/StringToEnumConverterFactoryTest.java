/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.converter;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

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