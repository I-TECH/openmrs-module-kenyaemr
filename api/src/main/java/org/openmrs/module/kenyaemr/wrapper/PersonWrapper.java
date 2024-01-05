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

import org.openmrs.Person;
import org.openmrs.module.kenyacore.wrapper.AbstractPersonWrapper;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;

/**
 * Wrapper class for persons.
 */
public class PersonWrapper extends AbstractPersonWrapper {

	/**
	 * Creates a new wrapper
	 * @param target the target
	 */
	public PersonWrapper(Person target) {
		super(target);
	}

	/**
	 * Gets the telephone contact
	 * @return the telephone number
	 */
	public String getTelephoneContact() {
		return getAsAttribute(CommonMetadata._PersonAttributeType.TELEPHONE_CONTACT);
	}

	/**
	 * Sets the telephone contact
	 * @param value the telephone number
	 */
	public void setTelephoneContact(String value) {
		setAsAttribute(CommonMetadata._PersonAttributeType.TELEPHONE_CONTACT, value);
	}

	/**
	 * Gets the email address
	 * @return the email address
	 */
	public String getEmailAddress() {
		return getAsAttribute(CommonMetadata._PersonAttributeType.EMAIL_ADDRESS);
	}

	/**
	 * Sets the email address
	 * @param value the email address
	 */
	public void setEmailAddress(String value) {
		setAsAttribute(CommonMetadata._PersonAttributeType.EMAIL_ADDRESS, value);
	}
}