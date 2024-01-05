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
 * Web related module constants
 */
public class EmrWebConstants {

	/**
	 * Time in milliseconds to lockout an IP or user after repeated
	 * failed login attempts
	 */
	public static final int FAILED_LOGIN_LOCKOUT_TIME = 300000; // 5 minutes

	/**
	 * Name of session attribute for temporary reset passwords
	 */
	public static final String SESSION_ATTR_RESET_PASSWORD = "resetPassword";

	/**
	 * Page model attributes
	 */
	public static final String MODEL_ATTR_CURRENT_PATIENT = "currentPatient";
	public static final String MODEL_ATTR_CURRENT_VISIT = "currentVisit";
	public static final String MODEL_ATTR_ACTIVE_VISIT = "activeVisit";

	/**
	 * Fragment identifiers
	 */
	public static final String PROGRAM_ENROLLMENT_SUMMARY_FRAGMENT = "enrollment-summary";
	public static final String PROGRAM_CARE_PANEL_FRAGMENT = "care-panel";
	public static final String PROGRAM_COMPLETION_SUMMARY_FRAGMENT = "completion-summary";
}