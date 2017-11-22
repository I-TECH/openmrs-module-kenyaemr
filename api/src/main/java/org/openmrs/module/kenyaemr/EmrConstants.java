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
 * KenyaEMR specific constants
 */
public class EmrConstants {

	/**
	 * Module ID
	 */
	public static final String MODULE_ID = "kenyaemr";

	/**
	 * Application IDs
	 */
	public static final String APP_REGISTRATION = MODULE_ID + ".registration";
	public static final String APP_INTAKE = MODULE_ID + ".intake";
	public static final String APP_CLINICIAN = MODULE_ID + ".medicalEncounter";
	public static final String APP_CHART = MODULE_ID + ".medicalChart";
	public static final String APP_REPORTS = MODULE_ID + ".reports";
	public static final String APP_DIRECTORY = MODULE_ID + ".directory";
	public static final String APP_FACILITIES = MODULE_ID + ".facilities";
	public static final String APP_ADMIN = MODULE_ID + ".admin";
	public static final String APP_DEVELOPER = MODULE_ID + ".developer";
	public static final String APP_FACILITY_DASHBOARD = MODULE_ID + ".facilityDashboard";

	/**
	 * Global property names
	 */
	public static final String GP_DEFAULT_LOCATION = MODULE_ID + ".defaultLocation";
	public static final String GP_CONTROLLER_WHITELIST = MODULE_ID + ".controllerWhitelist";
	public static final String GP_SUPPORT_PHONE_NUMBER = MODULE_ID + ".supportPhoneNumber";
	public static final String GP_SUPPORT_EMAIL_ADDRESS = MODULE_ID + ".supportEmailAddress";
	public static final String GP_EXTERNAL_HELP_URL = MODULE_ID + ".externalHelpUrl";

	/**
	 * Default global property values
	 */
	public static final String DEFAULT_SUPPORT_PHONE_NUMBER = "0800720701";
	public static final String DEFAULT_SUPPORT_EMAIL_ADDRESS = "help@kenyaemr.org";
	public static final String DEFAULT_EXTERNAL_HELP_URL = "/help";
}