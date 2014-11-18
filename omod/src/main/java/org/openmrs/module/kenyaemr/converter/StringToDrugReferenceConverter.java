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

import org.openmrs.module.kenyaemr.regimen.DrugReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts String to DrugReference
 */
@Component
public class StringToDrugReferenceConverter implements Converter<String, DrugReference> {

	/**
	 * @see org.springframework.core.convert.converter.Converter#convert(Object)
	 */
	@Override
    public DrugReference convert(String source) {
		String[] tokens = source.split("\\$");

		if (tokens.length != 2) {
			return null;
		}

		if (tokens[0].equals("C")) {
			return DrugReference.fromConceptUuid(tokens[1]);
		}
		else if (tokens[0].equals("D")) {
			return DrugReference.fromDrugUuid(tokens[1]);
		}
		else {
			return null;
		}
    }
}