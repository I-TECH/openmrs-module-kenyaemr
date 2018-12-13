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
 * Tests for {@link EmailAddressValidator}
 */
public class EmailAddressValidatorTest {

	/**
	 * @see EmailAddressValidator#supports(Class)
	 */
	@Test
	public void supports() {
		Assert.assertThat(new EmailAddressValidator().supports(String.class), is(true));
	}

	/**
	 * @see EmailAddressValidator#validate(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void validate() {
		Assert.assertThat(invokeValidator("test@example.com"), is(true));
		Assert.assertThat(invokeValidator("test.2@ex.mple.co"), is(true));
		Assert.assertThat(invokeValidator("te@ex.co"), is(true));

		Assert.assertThat(invokeValidator("@ex.co"), is(false));
		Assert.assertThat(invokeValidator("test@example"), is(false));
		Assert.assertThat(invokeValidator("test@.com"), is(false));
	}

	/**
	 * Validates the input string
	 * @param input the input
	 * @return whether input is valid
	 */
	private static boolean invokeValidator(String input) {
		Validator validator = new EmailAddressValidator();
		Errors errors = new BeanPropertyBindingResult("test", "test");
		validator.validate(input, errors);
		return !errors.hasErrors();
	}
}