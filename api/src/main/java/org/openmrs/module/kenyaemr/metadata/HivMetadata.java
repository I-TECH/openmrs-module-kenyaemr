/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.metadata;

import org.openmrs.PatientIdentifierType.LocationBehavior;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterType;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.form;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.globalProperty;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.patientIdentifierType;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.program;

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
		public static final String DRUG_ORDER = "7df67b83-1b84-4fe2-b1b7-794b4e9bfcc3";
		public static final String LAB_ORDER = "e1406e88-e9a9-11e8-9f32-f2801f1b9fd1";
		public static final String CCC_DEFAULTER_TRACING = "1495edf8-2df2-11e9-b210-d663bd873d93";
		public static final String ALCOHOL_AND_DRUGS_ABUSE = "4224f8bf-11b2-4e47-a958-1dbdfd7fa41d";
		public static final String GENDER_BASED_VIOLENCE = "f091b067-bea5-4657-8445-cfec05dc46a2";
		public static final String ENHANCED_ADHERENCE = "54df6991-13de-4efc-a1a9-2d5ac1b72ff8";
		public static final String ART_PREPARATION = "ec2a91e5-444a-4ca0-87f1-f71ddfaf57eb";


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
		public static final String DRUG_ORDER = "888dbabd-1c18-4653-82c2-e753415ab79a";
		public static final String TREATMENT_PREPARATION = "782a4263-3ac9-4ce8-b316-534571233f12";
		public static final String GBV_SCREENING = "03767614-1384-4ce3-aea9-27e2f4e67d01";
		public static final String ALCOHOL_AND_DRUGS_SCREENING = "7b1ec2d5-a4ad-4ffc-a0d3-ff1ea68e293c";
		public static final String ENHANCED_ADHERENCE_SCREENING = "c483f10f-d9ee-4b0d-9b8c-c24c1ec24701";
		public static final String CCC_DEFAULTER_TRACING = "a1a62d1e-2def-11e9-b210-d663bd873d93";

	}

	public static final class _PatientIdentifierType {
		public static final String UNIQUE_PATIENT_NUMBER = Metadata.IdentifierType.UNIQUE_PATIENT_NUMBER;
		public static final String KDoD_NUMBER = Metadata.IdentifierType.KDoD_NUMBER;

	}

	public static final class _Program {
		public static final String HIV = Metadata.Program.HIV;
	}

	/**
	 * @see org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {
		AdministrationService administrationService = Context.getAdministrationService();
		final String isKDoD = (administrationService.getGlobalProperty("kenyaemr.isKDoD"));

		install(encounterType("HIV Enrollment", "Enrollment onto HIV program", _EncounterType.HIV_ENROLLMENT));
		install(encounterType("HIV Consultation", "Collection of HIV-specific data during the main consultation", _EncounterType.HIV_CONSULTATION));
		install(encounterType("HIV Discontinuation", "Discontinuation from HIV program", _EncounterType.HIV_DISCONTINUATION));
		install(encounterType("ART Refill", "ART Refill encounter", _EncounterType.ART_REFILL));
		install(encounterType("Family and Partner Testing", "Family and Partner Testing", _EncounterType.FAMILY_AND_PARTNER_TESTING));
		install(encounterType("HIV Confirmation", "HIV Confirmatory Encounter", _EncounterType.HIV_CONFIRMATION));
		install(encounterType("Drug Order", "Drug Order", _EncounterType.DRUG_ORDER));
		install(encounterType("Lab Order", "Lab Order", _EncounterType.LAB_ORDER));
		install(encounterType("CCC Defaulter Tracing", "CCC Defaulter Tracing", _EncounterType.CCC_DEFAULTER_TRACING));
		install(encounterType("Alcohol and Drug Abuse Screening", "Alcohol and Drug Abuse Screening", _EncounterType.ALCOHOL_AND_DRUGS_ABUSE));
		install(encounterType("ART Preparation", "ART Preparation", _EncounterType.ART_PREPARATION));
		install(encounterType("Enhanced Adherence Screening", "Enhanced Adherence Screening", _EncounterType.ENHANCED_ADHERENCE));
		install(encounterType("Gender Based Violence Screening", "Gender Based Violence Screening", _EncounterType.GENDER_BASED_VIOLENCE));

		install(form("HIV Enrollment", null, _EncounterType.HIV_ENROLLMENT, "1", _Form.HIV_ENROLLMENT));
		install(form("Clinical Encounter - HIV addendum", null, _EncounterType.HIV_CONSULTATION, "1", _Form.CLINICAL_ENCOUNTER_HIV_ADDENDUM));
		install(form("Family History", null, CommonMetadata._EncounterType.REGISTRATION, "1", _Form.FAMILY_HISTORY));
		install(form("MOH 257 Face Page", null, _EncounterType.HIV_ENROLLMENT, "1", _Form.MOH_257_FACE_PAGE));
		install(form("MOH 257 ARV Therapy", null, _EncounterType.HIV_CONSULTATION, "1", _Form.MOH_257_ARV_THERAPY));
		install(form("MOH 257 Visit Summary", null, _EncounterType.HIV_CONSULTATION, "1", _Form.MOH_257_VISIT_SUMMARY));
		install(form("HIV Discontinuation", null, _EncounterType.HIV_DISCONTINUATION, "1", _Form.HIV_DISCONTINUATION));
		install(form("HIV Green Card", "Green Card Form", _EncounterType.HIV_CONSULTATION, "1", _Form.HIV_GREEN_CARD));
		install(form("ART Fast Track", "ART Fast Track Form", _EncounterType.ART_REFILL, "1", _Form.FAST_TRACK));
		install(form("Family and Partner Testing Results", "Family and Partner Testing for HIV Negative Patients", _EncounterType.FAMILY_AND_PARTNER_TESTING, "1", _Form.FAMILY_TESTING_FORM_FOR_NEGATIVE_CLIENTS));
		install(form("Drug Order", "Drug Order", _EncounterType.DRUG_ORDER, "1", _Form.DRUG_ORDER));
		install(form("ART Preparation", "ART Preparation", _EncounterType.ART_PREPARATION, "1", _Form.TREATMENT_PREPARATION));
		install(form("Gender Based Violence Screening", "Gender Based Violence Screening", _EncounterType.GENDER_BASED_VIOLENCE, "1", _Form.GBV_SCREENING));
		install(form("Alcohol and Drug Abuse Screening(CAGE-AID/CRAFFT)", "Alcohol and Drug Abuse Screening", _EncounterType.ALCOHOL_AND_DRUGS_ABUSE, "1", _Form.ALCOHOL_AND_DRUGS_SCREENING));
		install(form("Enhanced Adherence Screening", "Enhanced Adherence Screening", _EncounterType.ENHANCED_ADHERENCE, "1", _Form.ENHANCED_ADHERENCE_SCREENING));
		install(form("CCC Defaulter Tracing", "Defaulter Tracing Form", _EncounterType.CCC_DEFAULTER_TRACING, "1", _Form.CCC_DEFAULTER_TRACING));
		install(patientIdentifierType("KDoD number", "Unique Id for KDoD patient", "(?i)^(KDOD)+[0-9]{4,5}$", "Must start with KDoD followed by 4-5 digit number. Example: KDoD12345 or kdod1233",
				null, LocationBehavior.NOT_USED, false, _PatientIdentifierType.KDoD_NUMBER));
		if(isKDoD.equals("true")){

			//Validation for UPN (if present) is relaxed for KDOD patients because migrated UPNs have illegal formats
			install(patientIdentifierType("Unique Patient Number", "Assigned to every HIV patient", null, null,
					null, LocationBehavior.NOT_USED, false, _PatientIdentifierType.UNIQUE_PATIENT_NUMBER));
		}
		else{
			install(patientIdentifierType("Unique Patient Number", "Assigned to every HIV patient", "^[0-9]{10,11}$", "Facility code followed by sequential number",
					null, LocationBehavior.NOT_USED, false, _PatientIdentifierType.UNIQUE_PATIENT_NUMBER));
		}
		install(program("HIV", "Treatment for HIV-positive patients", Dictionary.HIV_PROGRAM, _Program.HIV));
		install(globalProperty(LDL_DEFAULT_VALUE, "Default value for LDL results. Required for graphing", "50"));

	}
}