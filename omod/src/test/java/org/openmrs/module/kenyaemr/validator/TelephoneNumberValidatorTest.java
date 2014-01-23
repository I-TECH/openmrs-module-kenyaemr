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