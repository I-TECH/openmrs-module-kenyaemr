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

package org.openmrs.module.kenyaemr.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator for telephone numbers
 */
public class TelephoneNumberValidator implements Validator {

	/**
	 * @see org.springframework.validation.Validator#supports(Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return String.class.equals(clazz);
	}

	/**
	 * @see org.springframework.validation.Validator#validate(Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object o, Errors errors) {
		String number = (String) o;
		number = number.trim();

		if (number.length() != 10) {
			errors.rejectValue(null, "Phone numbers must be 10 digits long");
		}
		if (!number.matches("\\d{10}")) {
			errors.rejectValue(null, "Phone numbers must only contain numbers");
		}
	}
}
