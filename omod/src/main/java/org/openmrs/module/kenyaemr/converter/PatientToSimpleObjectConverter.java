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

package org.openmrs.module.kenyaemr.converter;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.kenyaemr.KenyaEmrUiUtils;
import org.openmrs.module.kenyaemr.identifier.IdentifierManager;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts a location to a simple object
 */
@Component
public class PatientToSimpleObjectConverter implements Converter<Patient, SimpleObject> {

	@Autowired
	private UiUtils ui;

	@Autowired
	private KenyaEmrUiUtils kenyaEmrUi;

	@Autowired
	private IdentifierManager identifierManager;

	/**
	 * @see org.springframework.core.convert.converter.Converter#convert(Object)
	 */
	@Override
	public SimpleObject convert(Patient patient) {
		SimpleObject ret = new SimpleObject();
		ret.put("id", patient.getId());
		ret.put("gender", patient.getGender());

		// Add formatted name, age and birth date values
		ret.put("name", kenyaEmrUi.formatPersonName(patient.getPersonName()));
		ret.put("age", kenyaEmrUi.formatPersonAge(patient));
		ret.put("birthdate", kenyaEmrUi.formatPersonBirthdate(patient));

		// Add display identifiers
		List<SimpleObject> simpleIdentifiers = new ArrayList<SimpleObject>();
		for (PatientIdentifier identifier : identifierManager.getPatientDisplayIdentifiers(patient)) {
			simpleIdentifiers.add(ui.simplifyObject(identifier));
		}

		ret.put("identifiers", simpleIdentifiers);
		return ret;
	}
}