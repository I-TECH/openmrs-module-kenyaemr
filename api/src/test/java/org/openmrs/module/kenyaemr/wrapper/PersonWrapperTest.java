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

package org.openmrs.module.kenyaemr.wrapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.api.PersonService;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link PersonWrapper}
 */
public class PersonWrapperTest extends BaseModuleContextSensitiveTest {

	@Autowired
	private CommonMetadata commonMetadata;

	@Autowired
	private PersonService personService;

	/**
	 * Setup each test
	 */
	@Before
	public void setup() {
		commonMetadata.install();
	}

	/**
	 * @see PersonWrapper#setTelephoneContact(String)
	 */
	@Test
	public void setTelephoneContact() {
		Person person = personService.getPerson(7);
		PersonWrapper wrapper = new PersonWrapper(person);
		wrapper.setTelephoneContact("0123456789");

		personService.savePerson(person);

		List<PersonAttribute> attrs = person.getAttributes("Telephone contact");
		Assert.assertThat(attrs, hasSize(1));

		Assert.assertThat(wrapper.getTelephoneContact(), is("0123456789"));
	}

	/**
	 * @see PersonWrapper#setEmailAddress(String)
	 */
	@Test
	public void setEmailAddress() {
		Person person = personService.getPerson(7);
		PersonWrapper wrapper = new PersonWrapper(person);
		wrapper.setEmailAddress("test@example.com");

		personService.savePerson(person);

		List<PersonAttribute> attrs = person.getAttributes("Email address");
		Assert.assertThat(attrs, hasSize(1));

		Assert.assertThat(wrapper.getEmailAddress(), is("test@example.com"));
	}
}