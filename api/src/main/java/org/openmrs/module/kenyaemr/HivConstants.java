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
 * Constants related to HIV care
 */
public class HivConstants {

	/**
	 * Number of days without an encounter before a patient is consider lost to follow up
	 */
	public static final int LOST_TO_FOLLOW_UP_THRESHOLD_DAYS = 31; // the threshold has since changed to 31 days from 90 months

	/**
	 * Number of days between old CD4 count and need for new CD4 count
	 */
	public static final int NEEDS_CD4_COUNT_AFTER_DAYS = 180;

	/**
	 * Number of days between old CD4 count and current CD4 count to determine declining status
	 */
	public static final int DECLINING_CD4_COUNT_ACROSS_DAYS = 180;

}