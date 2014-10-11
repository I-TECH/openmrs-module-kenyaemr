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
 * The stage of a pregnant woman in the MCH-MS program ranging from before they enroll into the program to 3 days after
 * delivery. Note that some stages overlap over other stages.
 */
public enum PregnancyStage {

	BEFORE_ENROLLMENT, // The stage before actual enrollment into the MCH-MS program
	AFTER_ENROLLMENT, // Any stage ranging from when the patient is enrolled up to the postnatal stage
	ANTENATAL, // The stage from when the patient is enrolled to 3 days before the date of delivery (date of delivery included)
	DELIVERY, // The day of delivery and the 2 days preceding it
	POSTNATAL; // The stage from the end of the date of delivery to the end of 3 days thereafter
}