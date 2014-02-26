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

package org.openmrs.module.kenyaemr.chore;

import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.chore.AbstractChore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.lang.String;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Prior to 13.3.1, the EditPatientFragmentController let users double submit causing duplicate patient records, often
 * with duplicate identifiers
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
