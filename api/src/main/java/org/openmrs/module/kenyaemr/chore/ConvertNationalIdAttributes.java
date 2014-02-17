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

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.chore.AbstractChore;
import org.openmrs.module.kenyacore.chore.Requires;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * Prior to 13.3, National ID values were stored as attributes rather than as identifiers. This chore converts attributes
 * to identifiers if the identifier value isn't taken by another patient.
 */
@Component("kenyaemr.chore.convertNationalIdAttributes")
@Requires({ FixMissingOpenmrsIdentifiers.class })
public class ConvertNationalIdAttributes extends AbstractChore {

	@Autowired
	private KenyaEmrService kenyaEmrService;

	@Autowired
	private PatientService patientService;

	/**
	 * @see org.openmrs.module.kenyacore.chore.AbstractChore#perform(java.io.PrintWriter)
	 */
	@Override
	public void perform(PrintWriter output) {
		PersonAttributeType nidPerAttrType = Context.getPersonService().getPersonAttributeTypeByUuid("73d34479-2f9e-4de3-a5e6-1f79a17459bb");
		if (nidPerAttrType == null) {
			return; // Don't need to do anything as this must be a 13.3+ clean install
		}

		PatientIdentifierType nidPatIdType = MetadataUtils.getPatientIdentifierType(CommonMetadata._PatientIdentifierType.NATIONAL_ID);
		Location defaultLocation = kenyaEmrService.getDefaultLocation();

		Set<String> takenNidValues = new HashSet<String>();

		int converted = 0;

		for (Patient patient : patientService.getAllPatients()) {
			PatientIdentifier nidPatId = patient.getPatientIdentifier(nidPatIdType);
			PersonAttribute nidPerAttr = patient.getAttribute(nidPerAttrType);

			if (nidPatId == null && nidPerAttr != null && !takenNidValues.contains(nidPerAttr.getValue()) && valueCanBeConverted(nidPatIdType, nidPerAttr.getValue())) {
				nidPatId = new PatientIdentifier();
				nidPatId.setPatient(patient);
				nidPatId.setIdentifierType(nidPatIdType);
				nidPatId.setIdentifier(nidPerAttr.getValue());
				nidPatId.setLocation(defaultLocation);
				patient.addIdentifier(nidPatId);

				patientService.savePatientIdentifier(nidPatId);
				converted++;
			}

			if (nidPatId != null) {
				takenNidValues.add(nidPatId.getIdentifier());
			}

			// Void any attribute
			if (nidPerAttr != null) {
				nidPerAttr.setVoided(true);
				patientService.savePatient(patient);
			}
		}

		output.println("Converted " + converted + " national ID attributes to identifiers");
	}

	/**
	 * Helper method to check that the value from an attribute can be converted to an identifier
	 * @param value the value
	 * @return true if it can be converted
	 */
	protected boolean valueCanBeConverted(PatientIdentifierType idType, String value) {
		return value.matches(idType.getFormat());
	}
}