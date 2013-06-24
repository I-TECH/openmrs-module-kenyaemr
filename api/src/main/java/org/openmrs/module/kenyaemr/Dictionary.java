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

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.util.KenyaEmrUtils;

/**
 * Dictionary for concepts used by Kenya EMR
 */
public class Dictionary {

	/**
	 * Name of global property that stores database concepts version
	 */
	private static final String GP_CONCEPTS_VERSION = "ciel.conceptsVersion";

	/**
	 * Required concepts version
	 */
	public static final String REQUIRED_DATABASE_VERSION = "20130604";

	/**
	 * Gets the database version of the concepts
	 * @return the version
	 */
	public static String getDatabaseVersion() {
		return Context.getAdministrationService().getGlobalProperty(GP_CONCEPTS_VERSION);
	}

	/**
	 * Checks if the database version meets the requirements
	 * @return true if database version is sufficient
	 */
	public static boolean hasRequiredDatabaseVersion() {
		String conceptsVersion = Dictionary.getDatabaseVersion();
		return conceptsVersion != null && KenyaEmrUtils.checkCielVersions(Dictionary.REQUIRED_DATABASE_VERSION, conceptsVersion);
	}

	/**
	 * Gets a concept by an identifier (mapping or UUID)
	 * @param identifier the identifier
	 * @return the concept
	 * @throws IllegalArgumentException if no concept could be found
	 */
	public static Concept getConcept(String identifier) {
		Concept concept = null;

		if (identifier.contains(":")) {
			String[] tokens = identifier.split(":");
			concept = Context.getConceptService().getConceptByMapping(tokens[1].trim(), tokens[0].trim());
		}
		else {
			// Assume its a UUID
			concept = Context.getConceptService().getConceptByUuid(identifier);
		}

		if (concept == null) {
			throw new IllegalArgumentException("No concept with identifier '" + identifier + "'");
		}

		return concept;
	}

	// Concept identifiers (A-Z)
	public static final String ADULT_INPATIENT_SERVICE = "160536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String ANTENATAL_CASE_NUMBER = "161655AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String ANTIRETROVIRAL_DRUGS = "1085AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String ANTIRETROVIRAL_TREATMENT_START_DATE = "159599AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String CD4_COUNT = "5497AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String CD4_PERCENT = "730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String CIVIL_STATUS = "1054AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String COLLEGE_UNIVERSITY_POLYTECHNIC = "159785AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String CONDOMS_PROVIDED_DURING_VISIT = "159777AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String COUGH_LASTING_MORE_THAN_TWO_WEEKS = "159799AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String CURRENT_WHO_STAGE = "5356AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String CURRENTLY_USING_BIRTH_CONTROL = "965AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String DAPSONE = "74250AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String DATE_OF_HIV_DIAGNOSIS = "160554AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String DISEASE_SUSPECTED = "142177AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String EDUCATION = "1712AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String FAMILY_PLANNING = "160653AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String FLUCONAZOLE = "76488AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String MEDICATION_ORDERS = "1282AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String METHOD_OF_ENROLLMENT = "160540AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String METHOD_OF_FAMILY_PLANNING = "374AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String NATURAL_FAMILY_PLANNING = "5277AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String NEGATIVE = "664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String NO = "1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String NONE = "1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String NOT_APPLICABLE = "1175AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String OCCUPATION = "1542AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String OTHER_NON_CODED = "5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String PEDIATRIC_INPATIENT_SERVICE = "160537AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String PMTCT_PROGRAM = "160538AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String POSITIVE = "703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String PREGNANCY_STATUS = "5272AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String PRIMARY_EDUCATION = "1713AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String PULMONARY_TB = "42AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String REASON_FOR_PROGRAM_DISCONTINUATION = "161555AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String REFERRING_CLINIC_OR_HOSPITAL = "159371AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String RELAPSE_SMEAR_POSITIVE_TUBERCULOSIS = "159877AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String RESULTS_TUBERCULOSIS_CULTURE = "159982AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String RETREATMENT_AFTER_DEFAULT_TUBERCULOSIS = "159873AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String RETURN_VISIT_DATE = "5096AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String SCHEDULED_VISIT = "1246AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String SECONDARY_EDUCATION = "1714AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String SEXUAL_ABSTINENCE = "159524AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String SIGN_SYMPTOM_NAME = "1728AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String SIGN_SYMPTOM_PRESENT = "1729AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String SITE_OF_TUBERCULOSIS_DISEASE = "160040AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String SMEAR_POSITIVE_NEW_TUBERCULOSIS_PATIENT = "159878AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String SULFAMETHOXAZOLE_TRIMETHOPRIM = "105281AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String SPUTUM_FOR_ACID_FAST_BACILLI = "307AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String TESTS_ORDERED = "1271AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String TUBERCULOSIS_DISEASE_STATUS = "1659AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String TUBERCULOSIS_DRUG_TREATMENT_START_DATE = "1113AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String TUBERCULOSIS_TREATMENT_FAILURE = "159874AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String TUBERCULOSIS_TREATMENT_NUMBER = "161654AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String TUBERCULOSIS_TREATMENT_OUTCOME = "159786AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String TUBERCULOSIS_TREATMENT_PROGRAM = "160541AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String TRANSFER_IN_DATE = "160534AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String TYPE_OF_TB_PATIENT = "159871AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String TRANSFERRED_OUT = "159492AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String UNDER_FIVE_CLINIC = "160544AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String UNIVERSITY_COMPLETE = "160300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String VCT_PROGRAM = "160539AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String WEIGHT_KG = "5089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String WHO_STAGE_1_ADULT = "1204AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String WHO_STAGE_1_PEDS = "1220AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String WHO_STAGE_2_ADULT = "1205AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String WHO_STAGE_2_PEDS = "1221AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String WHO_STAGE_3_ADULT = "1206AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String WHO_STAGE_3_PEDS = "1222AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String WHO_STAGE_4_ADULT = "1207AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String WHO_STAGE_4_PEDS = "1223AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String YES = "1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	//marital status options
	public static final String SEPARATED = "1056AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String NEVER_MARRIED = "1057AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String DIVORCED = "1058AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String WIDOWED = "1059AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String LIVING_WITH_PARTNER = "1060AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String MARRIED_MONOGAMOUS = "5555AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String LIVES_ALONE = "135704AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	//public static final String OTHER_NON_CODED = "5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String MARRIED_POLYGAMOUS = "159715AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
}