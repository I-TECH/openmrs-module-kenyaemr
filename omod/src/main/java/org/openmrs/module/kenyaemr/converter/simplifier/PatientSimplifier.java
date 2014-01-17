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

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.kenyacore.identifier.IdentifierManager;
import org.openmrs.module.kenyaui.simplifier.AbstractSimplifier;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts a patient to a simple object
 */
@Component
public class PatientSimplifier extends AbstractSimplifier<Patient> {

	@Autowired
	private UiUtils ui;

	@Autowired
	private PersonSimplifier personSimplifier;

	@Autowired
	private IdentifierManager identifierManager;

	/**
	 * @see AbstractSimplifier#simplify(Object)
	 */
	@Override
	protected SimpleObject simplify(Patient patient) {
		// Convert as person first
		SimpleObject ret = personSimplifier.convert(patient);

		// Add display identifiers
		List<SimpleObject> simpleIdentifiers = new ArrayList<SimpleObject>();
		for (PatientIdentifier identifier : identifierManager.getPatientDisplayIdentifiers(patient)) {
			simpleIdentifiers.add(ui.simplifyObject(identifier));
		}

		ret.put("identifiers", simpleIdentifiers);
		return ret;
	}
}