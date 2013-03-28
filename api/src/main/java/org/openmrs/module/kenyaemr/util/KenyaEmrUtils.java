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
package org.openmrs.module.kenyaemr.util;

import org.openmrs.*;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.util.OpenmrsUtil;

import java.util.*;
import java.util.Date;

/**
 * Miscellaneous utility methods
 */
public class KenyaEmrUtils {

	/**
	 * Add days to an existing date
	 * @param date the date
	 * @param days the number of days to add (negative to subtract days)
	 * @return the new date
	 * @should shift the date by the number of days
	 */
	public static Date dateAddDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	/**
	 * Checks if two dates are the same day
	 * @param date1 the first date
	 * @param date2 the second date
	 * @return true if dates are same day
	 */
	public static boolean isSameDay(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return false;
		}

		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);

		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
				&& cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Checks if a given date is today
	 * @param date the date
	 * @return true if date is today
	 */
	public static boolean isToday(Date date) {
		return isSameDay(date, new Date());
	}

	/**
	 * Determines if patient is currently enrolled in the given program
	 * @param patient the patient
	 * @param program the program
	 * @return true if patient is currently in the program
	 */
	public static boolean isPatientInProgram(Patient patient, Program program) {
		ProgramWorkflowService pws = Context.getProgramWorkflowService();
		for (PatientProgram pp : pws.getPatientPrograms(patient, program, null, null, null, null, false)) {
			if (pp.getActive()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a visit has been entered retrospectively. Visits entered retrospectively are entered with just a single
	 * date value and are always stopped
	 * @param visit the visit
	 * @return true if visit was entered retrospectively
	 */
	public static boolean isRetrospectiveVisit(Visit visit) {
		if (visit.getStopDatetime() == null) {
			return false;
		}

		// Check that start is first second of day
		// Note that we don't compare milliseconds as these are lost in persistence
		Calendar start = Calendar.getInstance();
		start.setTime(visit.getStartDatetime());
		if (start.get(Calendar.HOUR_OF_DAY) != 0 || start.get(Calendar.MINUTE) != 0 || start.get(Calendar.SECOND) != 0) {
			return false;
		}

		// Check that stop is last second of day
		Calendar stop = Calendar.getInstance();
		stop.setTime(visit.getStopDatetime());
		if (stop.get(Calendar.HOUR_OF_DAY) != 23 || stop.get(Calendar.MINUTE) != 59 || stop.get(Calendar.SECOND) != 59) {
			return false;
		}

		// Check start is same day as stop
		return start.get(Calendar.YEAR) == stop.get(Calendar.YEAR)
				&& start.get(Calendar.MONTH) == stop.get(Calendar.MONTH)
				&& start.get(Calendar.DAY_OF_MONTH) == stop.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Checks a new visit to see if it overlaps with any other visit for that patient
	 * @param visit the new visit
	 * @return true if new visit will overlap
	 */
	public static boolean visitWillOverlap(Visit visit) {
		Patient patient = visit.getPatient();

		for (Visit existingVisit : Context.getVisitService().getVisitsByPatient(patient)) {
			// If visit exists in database, don't compare to itself
			if (existingVisit.getVisitId().equals(visit.getVisitId())) {
				continue;
			}

			if (OpenmrsUtil.compareWithNullAsLatest(visit.getStartDatetime(), existingVisit.getStopDatetime()) <= 0 &&
					OpenmrsUtil.compareWithNullAsLatest(visit.getStopDatetime(), existingVisit.getStartDatetime()) >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Converts a WHO stage concept to a WHO stage number
	 * @param c the WHO stage concept
	 * @return the WHO stage number (null if the concept isn't a WHO stage)
	 */
	public static Integer whoStage(Concept c) {
		if (c != null) {
			String uuid = c.getUuid();
			if (uuid.equals(MetadataConstants.WHO_STAGE_1_ADULT_CONCEPT_UUID) || uuid.equals(MetadataConstants.WHO_STAGE_1_PEDS_CONCEPT_UUID)) {
				return 1;
			}
			if (uuid.equals(MetadataConstants.WHO_STAGE_2_ADULT_CONCEPT_UUID) || uuid.equals(MetadataConstants.WHO_STAGE_2_PEDS_CONCEPT_UUID)) {
				return 2;
			}
			if (uuid.equals(MetadataConstants.WHO_STAGE_3_ADULT_CONCEPT_UUID) || uuid.equals(MetadataConstants.WHO_STAGE_3_PEDS_CONCEPT_UUID)) {
				return 3;
			}
			if (uuid.equals(MetadataConstants.WHO_STAGE_4_ADULT_CONCEPT_UUID) || uuid.equals(MetadataConstants.WHO_STAGE_4_PEDS_CONCEPT_UUID)) {
				return 4;
			}
		}
		return null;
	}
}