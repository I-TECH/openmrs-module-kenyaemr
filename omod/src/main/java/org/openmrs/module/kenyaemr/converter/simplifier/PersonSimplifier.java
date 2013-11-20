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

package org.openmrs.module.kenyaemr.converter.simplifier;

import org.openmrs.Person;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Converts a person to a simple object
 */
@Component
public class PersonSimplifier extends AbstractSimplifier<Person> {

	@Autowired
	private KenyaUiUtils kenyaUi;

	/**
	 * @see AbstractSimplifier#simplify(Object)
	 */
	@Override
	protected SimpleObject simplify(Person person) {
		SimpleObject ret = new SimpleObject();

		ret.put("id", person.getId());
		ret.put("name", kenyaUi.formatPersonName(person));
		ret.put("gender", person.getGender().toLowerCase());
		ret.put("isPatient", person.isPatient());

		// Add formatted age and birth date values
		if (person.getBirthdate() != null) {
			ret.put("birthdate", kenyaUi.formatPersonBirthdate(person));
			ret.put("age", kenyaUi.formatPersonAge(person));
		} else {
			ret.put("birthdate", null);
			ret.put("age", null);
		}

		return ret;
	}
}