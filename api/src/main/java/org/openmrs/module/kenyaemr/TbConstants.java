/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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