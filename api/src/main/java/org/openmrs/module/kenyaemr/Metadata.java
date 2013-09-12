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

	public static final class EncounterType {
		public static final String CHECK_OUT = "abfb143c-5b49-41e5-9ead-f47ee4cc57cf";
		public static final String CONSULTATION = "465a92f2-baf8-42e9-9612-53064be868e8";
		public static final String HIV_CONSULTATION = "a0034eee-1940-4e35-847f-97537a35d05e";
		public static final String HIV_DISCONTINUATION = "2bdada65-4c72-4a48-8730-859890e25cee";
		public static final String HIV_ENROLLMENT = "de78a6be-bfc5-4634-adc3-5f1a280455cc";
		public static final String LAB_RESULTS = "17a381d1-7e29-406a-b782-aa903b963c28";
		public static final String MCHCS_CONSULTATION = "bcc6da85-72f2-4291-b206-789b8186a021";
		public static final String MCHCS_DISCONTINUATION = "5feee3f1-aa16-4513-8bd0-5d9b27ef1208";
		public static final String MCHCS_ENROLLMENT = "415f5136-ca4a-49a8-8db3-f994187c3af6";
		public static final String MCHCS_IMMUNIZATION = "82169b8d-c945-4c41-be62-433dfd9d6c86";
		public static final String MCHMS_ENROLLMENT = "3ee036d8-7c13-4393-b5d6-036f2fe45126";
		public static final String MCHMS_CONSULTATION = "c6d09e05-1f25-4164-8860-9f32c5a02df0";
		public static final String MCHMS_DISCONTINUATION = "7c426cfc-3b47-4481-b55f-89860c21c7de";
		public static final String REGISTRATION = "de1f9d67-b73e-4e1b-90d0-036166fc6995";
		public static final String TB_CONSULTATION = "fbf0bfce-e9f4-45bb-935a-59195d8a0e35";
		public static final String TB_DISCONTINUATION = "d3e3d723-7458-4b4e-8998-408e8a551a84";
		public static final String TB_ENROLLMENT = "9d8498a4-372d-4dc4-a809-513a2434621e";
		public static final String TB_SCREENING = "ed6dacc9-0827-4c82-86be-53c0d8c449be";
		public static final String TRIAGE = "d1059fb9-a079-4feb-a749-eedd709ae542";
	}

	public static final class Form {
		public static final String CLINICAL_ENCOUNTER = "e958f902-64df-4819-afd4-7fb061f59308";
		public static final String CLINICAL_ENCOUNTER_HIV_ADDENDUM = "bd598114-4ef4-47b1-a746-a616180ccfc0";
		public static final String FAMILY_HISTORY = "7efa0ee0-6617-4cd7-8310-9f95dfee7a82";
		public static final String HIV_DISCONTINUATION = "e3237ede-fa70-451f-9e6c-0908bc39f8b9";
		public static final String HIV_ENROLLMENT = "e4b506c1-7379-42b6-a374-284469cba8da";
		public static final String LAB_RESULTS = "7e603909-9ed5-4d0c-a688-26ecb05d8b6e";
		public static final String MCHCS_ENROLLMENT = "8553d869-bdc8-4287-8505-910c7c998aff";
		public static final String MCHCS_DISCONTINUATION = "1dd02c43-904b-4206-8378-7b1a8414c326";
		public static final String MCHCS_FOLLOW_UP = "755b59e6-acbb-4853-abaf-be302039f902";
		public static final String MCHCS_IMMUNIZATION = "b4f3859e-861c-4a63-bdff-eb7392030d47";
		public static final String MOH_257_FACE_PAGE = "47814d87-2e53-45b1-8d05-ac2e944db64c";
		public static final String MOH_257_VISIT_SUMMARY = "23b4ebbd-29ad-455e-be0e-04aa6bc30798";
		public static final String OBSTETRIC_HISTORY = "8e4e1abf-7c08-4ba8-b6d8-19a9f1ccb6c9";
		public static final String OTHER_MEDICATIONS = "d4ff8ad1-19f8-484f-9395-04c755de9a47";
		public static final String PROGRESS_NOTE = "0038a296-62f8-4099-80e5-c9ea7590c157";
		public static final String TB_SCREENING = "59ed8e62-7f1f-40ae-a2e3-eabe350277ce";
		public static final String TB_ENROLLMENT = "89994550-9939-40f3-afa6-173bce445c79";
		public static final String TB_VISIT = "2daabb77-7ad6-4952-864b-8d23e109c69d";
		public static final String TB_COMPLETION = "4b296dd0-f6be-4007-9eb8-d0fd4e94fb3a";
		public static final String TRIAGE = "37f6bd8d-586a-4169-95fa-5781f987fe62";
	}

	public static final class Location {
		public static final String UNKNOWN = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f";
	}

	public static final class LocationAttributeType {
		public static final String MASTER_FACILITY_CODE = "8a845a89-6aa5-4111-81d3-0af31c45c002";
	}

	public static final class OrderType {
		public static final String DRUG = "131168f4-15f5-102d-96e4-000c29c2a5d7";
		public static final String LAB = "8ab58c6f-0d07-4a80-bb60-895639c1e66e";
	}

	public static final class PatientIdentifierType {
		public static final String NATIONAL_ID = "49af6cdc-7968-4abb-bf46-de10d7f4859f";
		public static final String OPENMRS_ID = "dfacd928-0370-4315-99d7-6ec1c9f7ae76";
		public static final String PATIENT_CLINIC_NUMBER = "b4d66522-11fc-45c7-83e3-39a1af21ae0d";
		public static final String UNIQUE_PATIENT_NUMBER = "05ee9cf4-7242-4a17-b4d4-00f707265c8a";
	}

	public static final class PersonAttributeType {
		public static final String NEXT_OF_KIN_ADDRESS = "7cf22bec-d90a-46ad-9f48-035952261294";
		public static final String NEXT_OF_KIN_CONTACT = "342a1d39-c541-4b29-8818-930916f4c2dc";
		public static final String NEXT_OF_KIN_NAME = "830bef6d-b01f-449d-9f8d-ac0fede8dbd3";
		public static final String NEXT_OF_KIN_RELATIONSHIP = "d0aa9fd1-2ac5-45d8-9c5e-4317c622c8f5";
		public static final String TELEPHONE_CONTACT = "b2c38640-2603-4629-aebd-3b54f33f1e3a";
	}

	public static final class Program {
		public static final String HIV = "dfdc6d40-2f2f-463d-ba90-cc97350441a8";
		public static final String MCHCS = "c2ecdf11-97cd-432a-a971-cfd9bd296b83";
		public static final String MCHMS = "b5d9e05f-f5ab-4612-98dd-adb75438ed34";
		public static final String TB = "9f144a34-3a4a-44a9-8486-6b7af6cc64f6";
	}

	public static final class Provider {
		public static final String UNKNOWN = "ae01b8ff-a4cc-4012-bcf7-72359e852e14";
	}

	public static final class VisitAttributeType {
		public static final String SOURCE_FORM = "8bfab185-6947-4958-b7ab-dfafae1a3e3d";
	}

	public static final class VisitType {
		public static final String OUTPATIENT = "3371a4d4-f66f-4454-a86d-92c7b3da990c";
	}
}