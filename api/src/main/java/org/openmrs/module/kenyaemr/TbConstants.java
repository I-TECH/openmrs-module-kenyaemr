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
 * Constants related to TB care
 */
public class TbConstants {

	/**
	 * Number of days in which a patient has to visit to be considered active
	 */
	public static final int PATIENT_ACTIVE_VISIT_THRESHOLD_DAYS = 90;

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