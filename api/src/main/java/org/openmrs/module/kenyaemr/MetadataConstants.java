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
	public static final String HIV_PROGRAM_ENROLLMENT_FORM_UUID = "e4b506c1-7379-42b6-a374-284469cba8da";
	public static final String HIV_PROGRAM_DISCONTINUATION_FORM_UUID = "e3237ede-fa70-451f-9e6c-0908bc39f8b9";
	public static final String TRIAGE_FORM_UUID = "37f6bd8d-586a-4169-95fa-5781f987fe62";
	public static final String CLINICAL_ENCOUNTER_FORM_UUID = "e958f902-64df-4819-afd4-7fb061f59308";
	public static final String CLINICAL_ENCOUNTER_HIV_ADDENDUM_FORM_UUID = "bd598114-4ef4-47b1-a746-a616180ccfc0";
	public static final String TB_SCREENING_FORM_UUID = "59ed8e62-7f1f-40ae-a2e3-eabe350277ce";
	public static final String PROGRESS_NOTE_FORM_UUID = "0038a296-62f8-4099-80e5-c9ea7590c157";
	public static final String OBSTETRIC_HISTORY_FORM_UUID = "8e4e1abf-7c08-4ba8-b6d8-19a9f1ccb6c9";
	public static final String FAMILY_HISTORY_FORM_UUID = "7efa0ee0-6617-4cd7-8310-9f95dfee7a82";
	public static final String MOH_257_ENCOUNTER_ORDER_LAB_INVESTIGATIONS_FORM_UUID = "7e603909-9ed5-4d0c-a688-26ecb05d8b6e";
	public static final String OTHER_MEDICATIONS_FORM_UUID = "d4ff8ad1-19f8-484f-9395-04c755de9a47";
	public static final String TB_ENROLLMENT_FORM_UUID = "89994550-9939-40f3-afa6-173bce445c79";
	public static final String TB_VISIT_FORM_UUID = "2daabb77-7ad6-4952-864b-8d23e109c69d";
	public static final String TB_COMPLETION_FORM_UUID = "4b296dd0-f6be-4007-9eb8-d0fd4e94fb3a";
			
	//public static final String MOH_257_ENCOUNTER_ART_HISTORY_FORM_UUID = "b388cd66-8e08-4e8f-97de-7cf10023ed0f";
	//public static final String MOH_257_ENCOUNTER_PAST_MEDICAL_AND_SURGICAL_HISTORY_FORM_UUID = "4a0d1332-490e-439f-9d14-c782143c94de";
	//public static final String MOH_257_ART_TREATMENT_INTERUPTION_FORM_UUID = "9e560607-cb99-46b0-8599-5d06110b4742";
	//public static final String MOH_257_ENCOUNTER_PREGNANCY_DETAILS_FORM_UUID = "c136f6e0-ecd0-4f9e-beeb-a5cab6161f44";
	//public static final String MOH_257_ENCOUNTER_PATIENTS_DEMOGRAPHICS_FORM_UUID = "8be821d9-8535-4715-9408-6c3112e8245a";        
	//public static final String MOH_257_ENCOUNTER_IMPRESSIONS_AND_DIAGNOSES_FORM_UUID = "46c5acb5-e4ae-4a4a-a9e5-6debf52cd773";
	//public static final String MOH_257_ENCOUNTER_DECISION_POINTS_FORM_UUID = "5ddb20c1-cd21-49cd-aba2-6bc62e367baf";         
	//public static final String MOH_257_ENCOUNTER_MEDICATION_ORDERS_FORM_UUID = "01f3aa8d-d0b5-4252-a7eb-059fec6633e9";            
	
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
	public static final String NATIONAL_ID_NUMBER_UUID = "73d34479-2f9e-4de3-a5e6-1f79a17459bb";
	
	// Concepts
	public static final String CD4_CONCEPT_UUID = "5497AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String CD4_PERCENT_CONCEPT_UUID = "730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String TRANSFER_IN_DATE_CONCEPT_UUID = "160534AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String ANTIRETROVIRAL_DRUGS_CONCEPT_UUID = "1085AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String ANTIRETROVIRAL_TREATMENT_START_DATE = "159599AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String CIVIL_STATUS_CONCEPT_UUID = "1054AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String OCCUPATION_CONCEPT_UUID = "1542AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String EDUCATION_CONCEPT_UUID = "1712AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String PRIMARY_EDUCATION_CONCEPT_UUID = "1713AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String SECONDARY_EDUCATION_CONCEPT_UUID = "1714AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String COLLEGE_UNIVERSITY_POLYTECHNIC_CONCEPT_UUID = "159785AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String UNIVERSITY_COMPLETE_CONCEPT_UUID = "160300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String NONE_CONCEPT_UUID = "1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String WHO_STAGE_CONCEPT_UUID = "5356AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String WHO_STAGE_1_ADULT_CONCEPT_UUID = "1204AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String WHO_STAGE_2_ADULT_CONCEPT_UUID = "1205AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String WHO_STAGE_3_ADULT_CONCEPT_UUID = "1206AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String WHO_STAGE_4_ADULT_CONCEPT_UUID = "1207AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String WHO_STAGE_1_PEDS_CONCEPT_UUID = "1220AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String WHO_STAGE_2_PEDS_CONCEPT_UUID = "1221AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String WHO_STAGE_3_PEDS_CONCEPT_UUID = "1222AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String WHO_STAGE_4_PEDS_CONCEPT_UUID = "1223AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String RETURN_VISIT_DATE_CONCEPT_UUID = "5096AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String DAPSONE_CONCEPT_UUID = "74250AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String MEDICATION_ORDERS_CONCEPT_UUID = "1282AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    public static final String SULFAMETHOXAZOLE_TRIMETHOPRIM_CONCEPT_UUID = "105281AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

    // Programs
	public static final String HIV_PROGRAM_UUID = "dfdc6d40-2f2f-463d-ba90-cc97350441a8";
	public static final String TB_PROGRAM_UUID = "9f144a34-3a4a-44a9-8486-6b7af6cc64f6";

    // Other
	public static final Locale LOCALE = Locale.UK;

}
