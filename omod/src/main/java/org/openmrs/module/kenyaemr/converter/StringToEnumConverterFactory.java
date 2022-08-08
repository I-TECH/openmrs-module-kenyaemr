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

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

/**
 * A factory for all string to enum converters
 */
@Component
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

	/**
	 * @see ConverterFactory#getConverter(Class)
	 */
	@Override
	public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
		return new StringToEnumConverter(targetType);
	}

	/**
	 * A generic string to enum converter class
	 * @param <T> the enum class
	 */
	private final class StringToEnumConverter<T extends Enum> implements Converter<String, T> {

		private Class<T> enumType;

		/**
		 * Creates a new converter
		 * @param enumType the enum class
		 */
		public StringToEnumConverter(Class<T> enumType) {
			this.enumType = enumType;
		}

		/**
		 * @see Converter#convert(Object)
		 */
		public T convert(String source) {
			if (StringUtils.isEmpty(source)) {
				return null;
			}

			return (T) Enum.valueOf(this.enumType, source.trim());
		}
	}
}