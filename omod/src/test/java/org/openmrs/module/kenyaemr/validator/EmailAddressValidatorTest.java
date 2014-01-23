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

import static org.hamcrest.Matchers.*;

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