/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.converter.simplifier;

import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.module.kenyaemr.wrapper.PersonWrapper;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.simplifier.AbstractSimplifier;
import org.openmrs.ui.framework.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Converts a person to a simple object
 */
@Component
public class PersonSimplifier extends AbstractSimplifier<Person> {

	@Autowired
	private KenyaUiUtils kenyaui;

	/**
	 * @see AbstractSimplifier#simplify(Object)
	 */
	@Override
	protected SimpleObject simplify(Person person) {
		SimpleObject ret = new SimpleObject();

		ret.put("id", person.getId());
		ret.put("name", kenyaui.formatPersonName(person));
		ret.put("gender", person.getGender().toLowerCase());
		ret.put("isPatient", person.isPatient());
		ret.put("dead", person.isDead());
		ret.put("deathDate", kenyaui.formatDateParam(person.getDeathDate()));
		ret.put("voided", person.isVoided());
		ret.put("dateVoided", kenyaui.formatDateParam(person.getDateVoided()));

		// Add formatted age and birth date values
		if (person.getBirthdate() != null) {
			ret.put("birthdate", kenyaui.formatPersonBirthdate(person));
			ret.put("age", kenyaui.formatPersonAge(person));
		} else {
			ret.put("birthdate", null);
			ret.put("age", null);
		}

		PersonWrapper wrapper = new PersonWrapper(person);
		ret.put("telephoneContact", wrapper.getTelephoneContact());
		ret.put("emailAddress", wrapper.getEmailAddress());

		return ret;
	}

	/**
	 * Gets a name for a person even if they are voided
	 * @param person the person
	 * @return the name
	 */
	protected PersonName getName(Person person) {
		if (!person.isVoided()) {
			return person.getPersonName();
		}
		else {
			// Get any name of a voided patient
			return (person.getNames().size() > 0) ? person.getNames().iterator().next() : null;
		}
	}
}