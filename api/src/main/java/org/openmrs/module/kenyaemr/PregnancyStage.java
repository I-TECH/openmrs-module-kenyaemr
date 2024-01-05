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