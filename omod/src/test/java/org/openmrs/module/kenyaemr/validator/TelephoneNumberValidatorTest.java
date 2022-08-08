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

import org.junit.Assert;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link TelephoneNumberValidator}
 */
public class TelephoneNumberValidatorTest {

	/**
	 * @see TelephoneNumberValidator#supports(Class)
	 */
	@Test
	public void supports() {
		Assert.assertThat(new TelephoneNumberValidator().supports(String.class), is(true));
	}

	/**
	 * @see TelephoneNumberValidator#validate(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void validate() {
		Assert.assertThat(invokeValidator("0123456789"), is(true));
		Assert.assertThat(invokeValidator("9876543210"), is(true));

		Assert.assertThat(invokeValidator("012345678"), is(false)); // Too short
		Assert.assertThat(invokeValidator("01234567891"), is(false)); // Too long
		Assert.assertThat(invokeValidator("0123x56789"), is(false)); // Contains non-numeric
	}

	/**
	 * Validates the input string
	 * @param input the input
	 * @return whether input is valid
	 */
	private static boolean invokeValidator(String input) {
		Validator validator = new TelephoneNumberValidator();
		Errors errors = new BeanPropertyBindingResult("test", "test");
		validator.validate(input, errors);
		return !errors.hasErrors();
	}
}