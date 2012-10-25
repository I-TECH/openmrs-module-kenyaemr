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
	 * Global property names
	 */
	public static final String GP_DEFAULT_LOCATION = "kenyaemr.defaultLocation";

	/**
	 * Number of days without an encounter before a patient is consider lost to follow up
	 */
	public static final int LOST_TO_FOLLOW_UP_THRESHOLD_DAYS = 90;

	/**
	 * Number of days between old CD4 count and current CD4 count to determine declining status
	 */
	public static final int DECLINING_CD4_COUNT_ACROSS_DAYS = 180;
}
