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
	public static final String MOH_257_REGISTRATION_FORM_UUID = "c7867d4f-6c10-4034-b37f-4da79e79af63";                                  
	//public static final String MOH_257_PATIENT_HISTORY_FORM_UUID = "960066b5-508d-4ca4-adb3-31859417544e";                               
	public static final String MOH_257_ENCOUNTER_TRIAGE_FORM_UUID = "37f6bd8d-586a-4169-95fa-5781f987fe62";                            
	public static final String MOH_257_TRANSFER_OUT_AND_DEATH_FORM_UUID = "1c34fb36-834a-4d11-b21e-4e75a968f07b";                        
	public static final String MOH_257_ART_TREATMENT_INTERUPTION_FORM_UUID = "9e560607-cb99-46b0-8599-5d06110b4742";                     
	public static final String MOH_257_ENROLLMENT_TRANSFER_IN_FORM_UUID = "6d60a39d-3963-4da8-854a-35939b67a473";                      
	public static final String MOH_257_ENROLLMENT_NEW_TO_HIV_CARE_FORM_UUID = "8515c87b-e9f5-430b-ad08-cc1f0fe1626d";                  
	public static final String MOH_257_ENCOUNTER_PREGNANCY_DETAILS_FORM_UUID = "c136f6e0-ecd0-4f9e-beeb-a5cab6161f44";                 
	public static final String MOH_257_ENCOUNTER_PATIENTS_DEMOGRAPHICS_FORM_UUID = "8be821d9-8535-4715-9408-6c3112e8245a";             
	public static final String MOH_257_ENCOUNTER_CLINICAL_NOTE_FORM_FORM_UUID = "0038a296-62f8-4099-80e5-c9ea7590c157";                
	public static final String MOH_257_ENCOUNTER_TB_SCREENING_FORM_UUID = "59ed8e62-7f1f-40ae-a2e3-eabe350277ce";                      
	public static final String MOH_257_ENCOUNTER_CLINICAL_ENCOUNTER_FORM_FORM_UUID = "e958f902-64df-4819-afd4-7fb061f59308";           
	public static final String MOH_257_ENCOUNTER_IMPRESSIONS_AND_DIAGNOSES_FORM_UUID = "46c5acb5-e4ae-4a4a-a9e5-6debf52cd773";         
	public static final String MOH_257_ENCOUNTER_DECISION_POINTS_FORM_UUID = "5ddb20c1-cd21-49cd-aba2-6bc62e367baf";                   
	public static final String MOH_257_ENCOUNTER_ORDER_LAB_INVESTIGATIONS_FORM_UUID = "7e603909-9ed5-4d0c-a688-26ecb05d8b6e";          
	public static final String MOH_257_ENCOUNTER_MEDICATION_ORDERS_FORM_UUID = "01f3aa8d-d0b5-4252-a7eb-059fec6633e9";                 
	public static final String MOH_257_ENCOUNTER_PAST_MEDICAL_AND_SURGICAL_HISTORY_FORM_UUID = "4a0d1332-490e-439f-9d14-c782143c94de"; 
	public static final String MOH_257_ENCOUNTER_ART_HISTORY_FORM_UUID = "b388cd66-8e08-4e8f-97de-7cf10023ed0f";                       
	
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
	public static final String TRANSFER_IN_DATE_CONCEPT_UUID = "160534AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	// Programs
	//public static final String HIV_PROGRAM_UUID = "3ccdc250-ca2c-4b27-8a5c-9c74c77353df";
	
	// Other
	public static final Locale LOCALE = Locale.UK;
	
}
