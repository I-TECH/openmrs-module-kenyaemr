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