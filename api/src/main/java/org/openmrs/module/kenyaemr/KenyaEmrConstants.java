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
 * Constants for everything that is _not_ {@link MetadataConstants}
 */
public class KenyaEmrConstants {

	/**
	 * Module ID
	 */
	public static final String MODULE_ID = "kenyaemr";

	/**
	 * Global property names
	 */
	public static final String GP_DEFAULT_LOCATION = "kenyaemr.defaultLocation";
	public static final String GP_CONCEPTS_VERSION = "ciel.conceptsVersion";

	/**
	 * Required concepts version
	 */
	public static final String REQUIRED_CONCEPTS_VERSION = "20130429";

	/**
	 * Number of days without an encounter before a patient is consider lost to follow up
	 */
	public static final int LOST_TO_FOLLOW_UP_THRESHOLD_DAYS = 90;

	/**
	 * Number of days between old CD4 count and need for new CD4 count
	 */
	public static final int NEEDS_CD4_COUNT_AFTER_DAYS = 180;

	/**
	 * Number of days between old CD4 count and current CD4 count to determine declining status
	 */
	public static final int DECLINING_CD4_COUNT_ACROSS_DAYS = 180;

	/**
	 * Number of days between sputum results duration taken before carrying out
	 * another sputum at month 2 for new patient classification
	 */
	public static final int MONTH_TWO_SPUTUM_TEST = 60;

	/**
	 * Number of days between sputum results duration taken before carrying out
	 * another sputum at month 5 for all patient classification
	 */
	public static final int MONTH_FIVE_SPUTUM_TEST = 150;

	/**
	 * Number of days between sputum results duration taken before carrying out
	 * another sputum at month 6 for new patient classification
	 */
	public static final int MONTH_SIX_SPUTUM_TEST = 180;

	/**
	 * Number of days between sputum results duration taken before carrying out
	 * another sputum at month 3 for smear positive relapse,failure and resumed
	 * patient classification
	 */
	public static final int MONTH_THREE_SPUTUM_TEST = 90;

	/**
	 * Number of days between sputum results duration taken before carrying out
	 * another sputum at month 8 for smear positive relapse,failure and resumed
	 * patient classification
	 */
	public static final int MONTH_EIGHT_SPUTUM_TEST = 240;
}