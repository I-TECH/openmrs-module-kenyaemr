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