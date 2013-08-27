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