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
 * Converts national ID person attributes to patient identifiers
 */

import org.openmrs.PatientIdentifier
import org.openmrs.api.context.Context

def nationalIdAttrType = Context.personService.getPersonAttributeTypeByUuid("73d34479-2f9e-4de3-a5e6-1f79a17459bb")
def nationalIdIdentifierType = Context.patientService.getPatientIdentifierTypeByUuid("49af6cdc-7968-4abb-bf46-de10d7f4859f")

def defaultLocationGP = Context.administrationService.getGlobalProperty("kenyaemr.defaultLocation")
def defaultLocation = defaultLocationGP ? Context.locationService.getLocation(defaultLocationGP.toInteger()) : null

// Check script can be run on this installation
if (!(nationalIdAttrType && nationalIdIdentifierType)) {
	return "This script is for upgrades to 13.3"
}
if (!defaultLocation) {
	return "Default location must be set"
}

// Go through each patient...
for (def patient : Context.patientService.allPatients) {
	def attribute = patient.getAttribute(nationalIdAttrType)
	def identifier = patient.getPatientIdentifier(nationalIdIdentifierType)

	// Only convert if attribute exists but identifier doesn't
	if (attribute && !identifier) {

		// Create identifier and assign to patient
		identifier = new PatientIdentifier()
		identifier.setPatient(patient)
		identifier.setIdentifierType(nationalIdIdentifierType)
		identifier.setIdentifier(attribute.value)
		identifier.setLocation(defaultLocation)
		patient.addIdentifier(identifier)

		Context.patientService.savePatientIdentifier(identifier)

		// Mark attribute as voided
		attribute.setVoided(true);
		attribute.setVoidedBy(Context.getAuthenticatedUser())
		attribute.setVoidReason("Converted to identifier")
		attribute.setDateVoided(new Date())

		Context.patientService.savePatient(patient)

		println "Converted national ID value '" + attribute.value + "' for patient " + patient.id
	}
}