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

/**
 * Checks all patients for duplicate or missing patient identifiers
 */

import org.openmrs.api.context.Context

def openmrsIdIdentifierType = Context.patientService.getPatientIdentifierTypeByUuid("dfacd928-0370-4315-99d7-6ec1c9f7ae76")

def duplicates = 0, missing = 0;

// Check for duplicate and missing identifier values
for (def patient : Context.patientService.allPatients) {
	def idTypes = [] as Set

	patient.activeIdentifiers.each { id ->
		if (idTypes.contains(id.identifierType)) {
			println "Patient #" + patient.id + " has duplicate identifier of type " + id.identifierType + " (value=" + id.identifier + ")"
			duplicates++
		} else {
			idTypes.add(id.identifierType)
		}
	}

	if (!idTypes.contains(openmrsIdIdentifierType)) {
		println "Patient #" + patient.id + " is missing the required OpenMRS ID"
		missing++
	}
}

println "=================== Summary ======================"
println "Duplicate ID problems: " + duplicates
println "Missing ID problems: " + missing