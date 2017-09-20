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

package org.openmrs.module.kenyaemr.metadata;

import org.openmrs.PatientIdentifierType.LocationBehavior;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.*;

/**
 * HIV metadata bundle
 */
@Component
@Requires({ CommonMetadata.class })
public class HivMetadata extends AbstractMetadataBundle {

	public static final String MODULE_ID = "kenyaemr";
	public static final String LDL_DEFAULT_VALUE = MODULE_ID + ".LDL_default_value";

	public static final class _EncounterType {
		public static final String HIV_CONSULTATION = "a0034eee-1940-4e35-847f-97537a35d05e";
		public static final String HIV_DISCONTINUATION = "2bdada65-4c72-4a48-8730-859890e25cee";
		public static final String HIV_ENROLLMENT = "de78a6be-bfc5-4634-adc3-5f1a280455cc";
		public static final String ART_REFILL = "e87aa2ad-6886-422e-9dfd-064e3bfe3aad";
		public static final String FAMILY_AND_PARTNER_TESTING = "975ae894-7660-4224-b777-468c2e710a2a";
		public static final String HIV_CONFIRMATION = "0c61819d-4f82-434e-b24d-aa8c82d49297";
	}

	public static final class _Form {
		public static final String CLINICAL_ENCOUNTER_HIV_ADDENDUM = "bd598114-4ef4-47b1-a746-a616180ccfc0";
		public static final String FAMILY_HISTORY = Metadata.Form.HIV_FAMILY_HISTORY;
		public static final String HIV_DISCONTINUATION = "e3237ede-fa70-451f-9e6c-0908bc39f8b9";
		public static final String HIV_ENROLLMENT = "e4b506c1-7379-42b6-a374-284469cba8da";
		public static final String MOH_257_FACE_PAGE = "47814d87-2e53-45b1-8d05-ac2e944db64c";
		public static final String MOH_257_ARV_THERAPY = "8f5b3ba5-1677-450f-8445-33b9a38107ae";
		public static final String MOH_257_VISIT_SUMMARY = "23b4ebbd-29ad-455e-be0e-04aa6bc30798";
		public static final String HIV_GREEN_CARD = "22c68f86-bbf0-49ba-b2d1-23fa7ccf0259";
		public static final String FAST_TRACK = "83fb6ab2-faec-4d87-a714-93e77a28a201";
		public static final String FAMILY_TESTING_FORM_FOR_NEGATIVE_CLIENTS = "62846fae-8d0b-4202-827e-8b6ffd30e587";

	}

	public static final class _PatientIdentifierType {
		public static final String UNIQUE_PATIENT_NUMBER = Metadata.IdentifierType.UNIQUE_PATIENT_NUMBER;
	}

	public static final class _Program {
		public static final String HIV = Metadata.Program.HIV;
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {
		install(encounterType("HIV Enrollment", "Enrollment onto HIV program", _EncounterType.HIV_ENROLLMENT));
		install(encounterType("HIV Consultation", "Collection of HIV-specific data during the main consultation", _EncounterType.HIV_CONSULTATION));
		install(encounterType("HIV Discontinuation", "Discontinuation from HIV program", _EncounterType.HIV_DISCONTINUATION));
		install(encounterType("ART Refill", "ART Refill encounter", _EncounterType.ART_REFILL));
		install(encounterType("Family and Partner Testing", "Family and Partner Testing", _EncounterType.FAMILY_AND_PARTNER_TESTING));
		install(encounterType("HIV Confirmation", "HIV Confirmatory Encounter", _EncounterType.HIV_CONFIRMATION));

		install(form("HIV Enrollment", null, _EncounterType.HIV_ENROLLMENT, "1", _Form.HIV_ENROLLMENT));
		install(form("Clinical Encounter - HIV addendum", null, _EncounterType.HIV_CONSULTATION, "1", _Form.CLINICAL_ENCOUNTER_HIV_ADDENDUM));
		install(form("Family History", null, CommonMetadata._EncounterType.REGISTRATION, "1", _Form.FAMILY_HISTORY));
		install(form("MOH 257 Face Page", null, _EncounterType.HIV_ENROLLMENT, "1", _Form.MOH_257_FACE_PAGE));
		install(form("MOH 257 ARV Therapy", null, _EncounterType.HIV_ENROLLMENT, "1", _Form.MOH_257_ARV_THERAPY));
		install(form("MOH 257 Visit Summary", null, _EncounterType.HIV_CONSULTATION, "1", _Form.MOH_257_VISIT_SUMMARY));
		install(form("HIV Discontinuation", null, _EncounterType.HIV_DISCONTINUATION, "1", _Form.HIV_DISCONTINUATION));
		install(form("HIV Green Card", "Green Card Form", _EncounterType.HIV_CONSULTATION, "1", _Form.HIV_GREEN_CARD));
		install(form("ART Fast Track", "ART Fast Track Form", _EncounterType.ART_REFILL, "1", _Form.FAST_TRACK));
		install(form("Family and Partner Testing Results", "Family and Partner Testing for HIV Negative Patients", _EncounterType.FAMILY_AND_PARTNER_TESTING, "1", _Form.FAMILY_TESTING_FORM_FOR_NEGATIVE_CLIENTS));

		install(patientIdentifierType("Unique Patient Number", "Assigned to every HIV patient", "^[0-9]{10}$", "Facility code followed by sequential number",
				null, LocationBehavior.NOT_USED, false, _PatientIdentifierType.UNIQUE_PATIENT_NUMBER));

		install(program("HIV", "Treatment for HIV-positive patients", Dictionary.HIV_PROGRAM, _Program.HIV));
		install(globalProperty(LDL_DEFAULT_VALUE, "Default value for LDL results. Required for graphing", "50"));
	}
}