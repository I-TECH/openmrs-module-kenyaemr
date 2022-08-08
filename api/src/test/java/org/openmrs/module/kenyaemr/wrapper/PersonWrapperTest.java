/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

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