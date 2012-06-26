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

import java.util.Locale;


/**
 *
 */
public class MetadataConstants {

	// Visit Types
	public static final String OUTPATIENT_VISIT_TYPE_UUID = "3371a4d4-f66f-4454-a86d-92c7b3da990c";
	
	// Encounter Types
	public static final String CHECK_OUT_ENCOUNTER_TYPE_UUID ="abfb143c-5b49-41e5-9ead-f47ee4cc57cf";
	public static final String TRIAGE_ENCOUNTER_TYPE_UUID = "d1059fb9-a079-4feb-a749-eedd709ae542";
	public static final String HIV_REGISTRATION_ENCOUNTER_TYPE_UUID = "de78a6be-bfc5-4634-adc3-5f1a280455cc";
	public static final String HIV_CONSULTATION_ENCOUNTER_TYPE_UUID = "a0034eee-1940-4e35-847f-97537a35d05e";
	public static final String LAB_RESULTS_ENCOUNTER_TYPE_UUID = "17a381d1-7e29-406a-b782-aa903b963c28";
	public static final String CONSULTATION_ENCOUNTER_TYPE_UUID = "465a92f2-baf8-42e9-9612-53064be868e8";
	public static final String REGISTRATION_ENCOUNTER_TYPE_UUID = "de1f9d67-b73e-4e1b-90d0-036166fc6995";
	
	// HTML Forms
	public static final String CLINICAL_ENCOUNTER_HTML_FORM_UUID = "02735db9-85cf-444e-b9ef-d59f5a27628d";
	public static final String LAB_RESULTS_HTML_FORM_UUID = "0c4bbc81-9bb9-4d04-b063-255d1cb58c48";
	public static final String HIV_PROGRAM_ENROLLMENT_HTML_FORM_UUID = "1a2bf46f-dffc-404f-9df3-96dacc394c49";
	public static final String FAMILY_PLANNING_AND_PREGNANCY_HTML_FORM_UUID = "681f7971-72de-4340-a39c-7147df5a24b5";
	public static final String ART_HISTORY_HTML_FORM_UUID = "6aad25ae-142c-4af7-9f46-f3e65ad67882";
	public static final String TB_SCREENING_HTML_FORM_UUID = "928f1e66-3b74-4e89-a31a-a08bcef4416c";
	public static final String VITALS_AND_TRIAGE_HTML_FORM_UUID = "9da2a73f-49f4-46ae-a8cc-49d1c0c49a1b";
	public static final String IMPRESSIONS_AND_DIAGNOSES_HTML_FORM_UUID = "a6591753-dd57-4e32-9034-d1710b147f4c";
	public static final String PAST_MEDICAL_HISTORY_AND_SURGICAL_HISTORY_HTML_FORM_UUID = "f3d7ba6a-e7ff-40b5-aeb1-73e1ac76bd20";
	
	// Locations
	public static final String UNKNOWN_LOCATION_UUID = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f";
	
	// Location Attribute Types
	public static final String MASTER_FACILITY_CODE_LOCATION_ATTRIBUTE_TYPE_UUID = "8a845a89-6aa5-4111-81d3-0af31c45c002";
	
	// Patient Identifier Types
	public static final String UNIQUE_PATIENT_NUMBER_UUID = "05ee9cf4-7242-4a17-b4d4-00f707265c8a";
	public static final String PATIENT_CLINIC_NUMBER_UUID = "b4d66522-11fc-45c7-83e3-39a1af21ae0d";
	public static final String OPENMRS_ID_UUID = "dfacd928-0370-4315-99d7-6ec1c9f7ae76";
	
	// Person Attribute Types
	public static final String TELEPHONE_CONTACT_UUID = "b2c38640-2603-4629-aebd-3b54f33f1e3a";
	
	// Concepts
	public static final String CD4_CONCEPT_UUID = "5497AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	// Programs
	//public static final String HIV_PROGRAM_UUID = "3ccdc250-ca2c-4b27-8a5c-9c74c77353df";
	
	// Other
	public static final Locale LOCALE = Locale.UK;
	
}
