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
	 * Fragment identifiers
	 */
	public static final String PROGRAM_ENROLLMENT_SUMMARY_FRAGMENT = "enrollment-summary";
	public static final String PROGRAM_CARE_PANEL_FRAGMENT = "care-panel";
	public static final String PROGRAM_COMPLETION_SUMMARY_FRAGMENT = "completion-summary";
}