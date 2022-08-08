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
import org.openmrs.ConceptClass;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts string to concept class
 */
@Component
public class StringToConceptClassConverter implements Converter<String, ConceptClass> {

	/**
	 * @see org.springframework.core.convert.converter.Converter#convert(Object)
	 */
	@Override
	public ConceptClass convert(String s) {
		return StringUtils.isEmpty(s) ? null : Context.getConceptService().getConceptClass(Integer.valueOf(s));
	}
}