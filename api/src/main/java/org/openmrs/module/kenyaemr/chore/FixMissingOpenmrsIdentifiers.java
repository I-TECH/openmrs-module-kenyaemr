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
import org.openmrs.api.PatientService;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.kenyacore.chore.AbstractChore;
import org.openmrs.module.kenyacore.chore.Requires;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Prior to 13.3.1, the EditPatientFragmentController appears to have sometimes saved a patient without properly saving
 * their required OpenMRS ID / MRN. This chore also fixes patient records with no preferred ID.
 */
@Component("kenyaemr.chore.fixMissingOpenmrsIdentifiers")
@Requires({ VoidDuplicateIdentifiers.class })
public class FixMissingOpenmrsIdentifiers extends AbstractChore {

	@Autowired
	private KenyaEmrService kenyaEmrService;

	@Autowired
	private PatientService patientService;

	@Autowired
	private IdentifierSourceService idgenService;

	/**
	 * @see org.openmrs.module.kenyacore.chore.AbstractChore#perform(java.io.PrintWriter)
	 */
	@Override
	public void perform(PrintWriter output) {
		Location defaultLocation = kenyaEmrService.getDefaultLocation();

		if (defaultLocation == null) {
			return; // Database is obviously clean as this hasn't yet been configured
		}

		PatientIdentifierType openmrsIdType = MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.OPENMRS_ID);
		IdentifierSource openmrsIdSource = idgenService.getAutoGenerationOption(openmrsIdType).getSource();

		List<Patient> allPatients = patientService.getAllPatients();
		Map<Patient, PatientIdentifier> patientsWithOpenmrsID = new HashMap<Patient, PatientIdentifier>();

		for (Patient patient : allPatients) {
			PatientIdentifier openmrsID = patient.getPatientIdentifier(openmrsIdType);
			if (openmrsID != null) {
				patientsWithOpenmrsID.put(patient, patient.getPatientIdentifier(openmrsIdType));
			}
		}

		int missingOpenmrsIDs = allPatients.size() - patientsWithOpenmrsID.size();

		// Batch generation of identifiers is a lot faster than one-by-one generation
		List<String> generatedIds = idgenService.generateIdentifiers(openmrsIdSource, missingOpenmrsIDs, FixMissingOpenmrsIdentifiers.class.getSimpleName());

		int fixedMissing = 0, fixedNoPreferred = 0;

		for (Patient patient : allPatients) {
			PatientIdentifier openmrsID = patientsWithOpenmrsID.get(patient);
			boolean needsSaved = false;

			// Generate new OpenMRS ID if needed
			if (openmrsID == null) {
				String generated = generatedIds.get(fixedMissing);
				openmrsID = new PatientIdentifier(generated, openmrsIdType, defaultLocation);
				patient.addIdentifier(openmrsID);

				fixedMissing++;
				needsSaved = true;
			}

			// Every patient needs one preferred ID although we don't use this in KenyaEMR
			if (!patientHasPreferredId(patient)) {
				openmrsID.setPreferred(true);

				fixedNoPreferred++;
				needsSaved = true;
			}

			if (needsSaved) {
				patientService.savePatientIdentifier(openmrsID);
			}
		}

		output.println("Fixed " + fixedMissing + " missing OpenMRS IDs");
		output.println("Fixed " + fixedNoPreferred + " patients with no preferred ID");
	}

	/**
	 * Helper method to determine if a patient has a preferred ID
	 * @param patient the patient
	 * @return true if they have a preferred ID
	 */
	protected boolean patientHasPreferredId(Patient patient) {
		PatientIdentifier defId = patient.getPatientIdentifier();
		return defId != null && defId.isPreferred();
	}
}
