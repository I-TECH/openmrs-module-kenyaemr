/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator for email addresses
 */
public class EmailAddressValidator implements Validator {

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
		String address = (String) o;
		address = address.trim();

		if (!address.matches("^[\\w\\.-]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
			errors.rejectValue(null, "Invalid address format");
		}
	}
}
