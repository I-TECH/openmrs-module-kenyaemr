/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.chore;

import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.chore.AbstractChore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * regimens.xml contains regimen groups that can be used to aid order groups. This chore populates order sets from regimens.xml
 */
@Component("kenyaemr.chore.voidDuplicateIdentifiers")
public class VoidDuplicateIdentifiers extends AbstractChore {

	@Autowired
	private PatientService patientService;

	/**
	 * @see org.openmrs.module.kenyacore.chore.AbstractChore#perform(java.io.PrintWriter)
	 */
	@Override
	public void perform(PrintWriter output) {
		for (PatientIdentifierType type : Context.getPatientService().getAllPatientIdentifierTypes()) {
			voidDuplicatesOfType(type, output);
		}
	}

	/**
	 * Voids all duplicate identifiers of the given type
	 * @param type the patient identifier type
	 */
	protected void voidDuplicatesOfType(PatientIdentifierType type, PrintWriter output) {
		List<PatientIdentifier> allOfType = patientService.getPatientIdentifiers(null, Collections.singletonList(type), null, null, null);
		Set<String> values = new HashSet<String>();
		int voided = 0;

		for (PatientIdentifier identifier : allOfType) {
			if (values.contains(identifier.getIdentifier())) {
				patientService.voidPatientIdentifier(identifier, "Duplicate");
				voided++;
			}
			else {
				values.add(identifier.getIdentifier());
			}
		}

		output.println("Voided " + voided + " duplicate '" + type.getName() + "' identifiers");
	}
}
