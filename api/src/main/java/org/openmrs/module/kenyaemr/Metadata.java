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

package org.openmrs.module.kenyaemr;

/**
 * Provides access to the core metadata in KenyaEMR
 */
public class Metadata {

	// Encounter Types
	public static final String CHECK_OUT_ENCOUNTER_TYPE ="abfb143c-5b49-41e5-9ead-f47ee4cc57cf";
	public static final String CONSULTATION_ENCOUNTER_TYPE = "465a92f2-baf8-42e9-9612-53064be868e8";
	public static final String HIV_CONSULTATION_ENCOUNTER_TYPE = "a0034eee-1940-4e35-847f-97537a35d05e";
	public static final String HIV_DISCONTINUATION_ENCOUNTER_TYPE = "2bdada65-4c72-4a48-8730-859890e25cee";
	public static final String HIV_ENROLLMENT_ENCOUNTER_TYPE = "de78a6be-bfc5-4634-adc3-5f1a280455cc";
	public static final String LAB_RESULTS_ENCOUNTER_TYPE = "17a381d1-7e29-406a-b782-aa903b963c28";
	public static final String REGISTRATION_ENCOUNTER_TYPE = "de1f9d67-b73e-4e1b-90d0-036166fc6995";
	public static final String TB_CONSULTATION_ENCOUNTER_TYPE = "fbf0bfce-e9f4-45bb-935a-59195d8a0e35";
	public static final String TB_DISCONTINUATION_ENCOUNTER_TYPE = "d3e3d723-7458-4b4e-8998-408e8a551a84";
	public static final String TB_ENROLLMENT_ENCOUNTER_TYPE = "9d8498a4-372d-4dc4-a809-513a2434621e";
	public static final String TB_SCREENING_ENCOUNTER_TYPE = "ed6dacc9-0827-4c82-86be-53c0d8c449be";
	public static final String TRIAGE_ENCOUNTER_TYPE = "d1059fb9-a079-4feb-a749-eedd709ae542";
	public static final String HEI_ENROLLMENT_ENCOUNTER_TYPE = "415f5136-ca4a-49a8-8db3-f994187c3af6";
	public static final String HEI_DISCONTINUATION_ENCOUNTER_TYPE = "5feee3f1-aa16-4513-8bd0-5d9b27ef1208";
	public static final String HEI_CONSULTATION_ENCOUNTER_TYPE = "bcc6da85-72f2-4291-b206-789b8186a021";
	public static final String HEI_IMMUNIZATION_ENCOUNTER_TYPE = "82169b8d-c945-4c41-be62-433dfd9d6c86";


	// Forms
	public static final String HIV_PROGRAM_ENROLLMENT_FORM = "e4b506c1-7379-42b6-a374-284469cba8da";
	public static final String HIV_PROGRAM_DISCONTINUATION_FORM = "e3237ede-fa70-451f-9e6c-0908bc39f8b9";
	public static final String TRIAGE_FORM = "37f6bd8d-586a-4169-95fa-5781f987fe62";
	public static final String CLINICAL_ENCOUNTER_FORM = "e958f902-64df-4819-afd4-7fb061f59308";
	public static final String CLINICAL_ENCOUNTER_HIV_ADDENDUM_FORM = "bd598114-4ef4-47b1-a746-a616180ccfc0";
	public static final String PROGRESS_NOTE_FORM = "0038a296-62f8-4099-80e5-c9ea7590c157";
	public static final String OBSTETRIC_HISTORY_FORM = "8e4e1abf-7c08-4ba8-b6d8-19a9f1ccb6c9";
	public static final String FAMILY_HISTORY_FORM = "7efa0ee0-6617-4cd7-8310-9f95dfee7a82";
	public static final String LAB_RESULTS_FORM = "7e603909-9ed5-4d0c-a688-26ecb05d8b6e";
	public static final String OTHER_MEDICATIONS_FORM = "d4ff8ad1-19f8-484f-9395-04c755de9a47";
	public static final String TB_SCREENING_FORM = "59ed8e62-7f1f-40ae-a2e3-eabe350277ce";
	public static final String TB_ENROLLMENT_FORM = "89994550-9939-40f3-afa6-173bce445c79";
	public static final String TB_VISIT_FORM = "2daabb77-7ad6-4952-864b-8d23e109c69d";
	public static final String TB_COMPLETION_FORM = "4b296dd0-f6be-4007-9eb8-d0fd4e94fb3a";
	public static final String MOH_257_VISIT_SUMMARY_FORM = "23b4ebbd-29ad-455e-be0e-04aa6bc30798";
	public static final String HEI_ENROLLMENT_FORM = "8553d869-bdc8-4287-8505-910c7c998aff";
	public static final String HEI_DISCONTINUATION_FORM = "1dd02c43-904b-4206-8378-7b1a8414c326";
	public static final String HEI_DISCONTINUATION_FORM = "1dd02c43-904b-4206-8378-7b1a8414c326";
	public static final String HEI_FOLLOW_UP_FORM = "755b59e6-acbb-4853-abaf-be302039f902";
	public static final String HEI_IMMUNIZATION_FORM = "b4f3859e-861c-4a63-bdff-eb7392030d47";


	// Locations
	public static final String UNKNOWN_LOCATION = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f";

	// Location Attribute Types
	public static final String MASTER_FACILITY_CODE_LOCATION_ATTRIBUTE_TYPE = "8a845a89-6aa5-4111-81d3-0af31c45c002";

	// Order Types
	public static final String DRUG_ORDER_TYPE = "131168f4-15f5-102d-96e4-000c29c2a5d7";
	public static final String LAB_ORDER_TYPE = "8ab58c6f-0d07-4a80-bb60-895639c1e66e";

	// Patient Identifier types
	public static final String UNIQUE_PATIENT_NUMBER_IDENTIFIER_TYPE = "05ee9cf4-7242-4a17-b4d4-00f707265c8a";
	public static final String PATIENT_CLINIC_NUMBER_IDENTIFIER_TYPE = "b4d66522-11fc-45c7-83e3-39a1af21ae0d";
	public static final String OPENMRS_ID_IDENTIFIER_TYPE = "dfacd928-0370-4315-99d7-6ec1c9f7ae76";

	// Person Attribute Types
	public static final String TELEPHONE_CONTACT_PERSON_ATTRIBUTE_TYPE = "b2c38640-2603-4629-aebd-3b54f33f1e3a";
	public static final String NATIONAL_ID_NUMBER_PERSON_ATTRIBUTE_TYPE = "73d34479-2f9e-4de3-a5e6-1f79a17459bb";
	public static final String NAME_OF_NEXT_OF_KIN_PERSON_ATTRIBUTE_TYPE = "830bef6d-b01f-449d-9f8d-ac0fede8dbd3";
	public static final String NEXT_OF_KIN_RELATIONSHIP_PERSON_ATTRIBUTE_TYPE = "d0aa9fd1-2ac5-45d8-9c5e-4317c622c8f5";
	public static final String NEXT_OF_KIN_CONTACT_PERSON_ATTRIBUTE_TYPE = "342a1d39-c541-4b29-8818-930916f4c2dc";
	public static final String NEXT_OF_KIN_ADDRESS_PERSON_ATTRIBUTE_TYPE = "7cf22bec-d90a-46ad-9f48-035952261294";

	// Programs
	public static final String HIV_PROGRAM = "dfdc6d40-2f2f-463d-ba90-cc97350441a8";
	public static final String TB_PROGRAM = "9f144a34-3a4a-44a9-8486-6b7af6cc64f6";

	// Providers
	public static final String UNKNOWN_PROVIDER = "ae01b8ff-a4cc-4012-bcf7-72359e852e14";

	// Visit Types
	public static final String OUTPATIENT_VISIT_TYPE = "3371a4d4-f66f-4454-a86d-92c7b3da990c";
}