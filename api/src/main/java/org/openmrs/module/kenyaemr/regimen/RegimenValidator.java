/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.regimen;

import org.openmrs.annotation.Handler;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validates a regimen
 */
@Handler(supports = { Regimen.class }, order = 50)
public class RegimenValidator implements Validator {

	/**
	 * @see Validator#supports(Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return Regimen.class.isAssignableFrom(clazz);
	}

	/**
	 * @see Validator#validate(Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object obj, Errors errors) {
		Regimen regimen = (Regimen)obj;

		if (regimen.getComponents().size() < 1) {
			errors.rejectValue(null, "Must contain at least one component");
		}

		for (RegimenComponent component : regimen.getComponents()) {
		 	if (!component.isComplete()) {
				errors.rejectValue(null, "Contains incomplete components");
				break;
			}
		}
	}
}