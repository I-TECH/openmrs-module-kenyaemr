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
package org.openmrs.module.kenyaemr.test;

import org.junit.Ignore;
import org.openmrs.*;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Utility methods for unit tests
 */
@Ignore
public class TestUtils {

	/**
	 * Convenience method to create a new date
	 * @param year the year
	 * @param month the month
	 * @param day the day
	 * @return the date
	 */
	public static Date date(int year, int month, int day) {
		return new GregorianCalendar(year, month - 1, day).getTime();
	}

	/**
	 * Convenience method to create and save an encounter
	 * @param patient the patient
	 * @param type the encounter type
	 * @param date the encounter date
	 * @return the saved encounter
	 */
	public static Encounter saveEncounter(Patient patient, EncounterType type, Date date) {
		Encounter encounter = new Encounter();
		encounter.setPatient(patient);
		encounter.setProvider(Context.getUserService().getUser(1)); // Super user
		encounter.setLocation(Context.getLocationService().getLocation(1)); // Unknown Location
		encounter.setEncounterType(type);
		encounter.setEncounterDatetime(date);
		return Context.getEncounterService().saveEncounter(encounter);
	}

	/**
	 * Convenience method to enroll patient on a program
	 * @param patient the patient
	 * @param program the program
	 * @param date the enroll date
	 * @return the patient program
	 */
	public static PatientProgram enrollInProgram(Patient patient, Program program, Date date) {
		PatientProgram pp = new PatientProgram();
		pp.setPatient(patient);
		pp.setProgram(program);
		pp.setDateEnrolled(date);
		return Context.getProgramWorkflowService().savePatientProgram(pp);
	}
}
