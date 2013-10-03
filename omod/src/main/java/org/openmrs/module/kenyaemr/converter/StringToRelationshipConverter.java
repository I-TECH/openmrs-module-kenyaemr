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
import org.openmrs.Relationship;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Convert from {@link String} to {@link org.openmrs.Relationship}, interpreting it as a Relationship.id
 */
@Component
public class StringToRelationshipConverter implements Converter<String, Relationship> {

	/**
	 * @see org.springframework.core.convert.converter.Converter#convert(Object)
	 */
	@Override
	public Relationship convert(String source) {
		if (StringUtils.isEmpty(source)) {
			return null;
		}

		return Context.getPersonService().getRelationship(Integer.valueOf(source));
	}
}