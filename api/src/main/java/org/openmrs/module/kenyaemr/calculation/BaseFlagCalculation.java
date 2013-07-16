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

package org.openmrs.module.kenyaemr.calculation;

/**
 * Calculation class which is is treated as a flag
 */
public abstract class BaseFlagCalculation extends BaseEmrCalculation {

	/**
	 * Gets a message suitable for a single patient or flag, e.g. "Eligible for ART"
	 * @return the message
	 */
	public abstract String getFlagMessage();
}