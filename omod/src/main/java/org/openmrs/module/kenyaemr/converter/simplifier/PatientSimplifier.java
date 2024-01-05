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