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

import org.junit.Assert;
import org.junit.Ignore;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.renderer.TsvReportRenderer;

import java.io.IOException;
import java.util.*;

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
	 * Create and save an encounter
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
	 * Enroll a patient in a program
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

	/**
	 * Save a numeric obs
	 * @param patient the patient
	 * @param concept the concept
	 * @param val the numeric value
	 * @param date the date
	 * @return the obs
	 */
	public static Obs saveObs(Patient patient, Concept concept, double val, Date date) {
		Obs obs = new Obs(patient, concept, date, null);
		obs.setValueNumeric(val);
		return Context.getObsService().saveObs(obs, null);
	}

	/**
	 * Save a datetime obs
	 * @param patient the patient
	 * @param concept the concept
	 * @param val the datetime value
	 * @param date the date
	 * @return the obs
	 */
	public static Obs saveObs(Patient patient, Concept concept, Date val, Date date) {
		Obs obs = new Obs(patient, concept, date, null);
		obs.setValueDatetime(val);
		return Context.getObsService().saveObs(obs, null);
	}

	/**
	 * Checks a patient alert list report
	 * @param expectedPatientIdentifiers the set of HIV identifiers of expected patients
	 * @param identifierColumn the name of column containing patient identifiers
	 * @param data the report data
	 */
	public static void checkPatientAlertListReport(Set<String> expectedPatientIdentifiers, String identifierColumn, ReportData data) {
		// Check report has one data set
		Assert.assertEquals(1, data.getDataSets().values().size());
		DataSet set = data.getDataSets().values().iterator().next();

		// Make mutable copy
		expectedPatientIdentifiers = new HashSet<String>(expectedPatientIdentifiers);

		// Check the patient name of each row is in the expected set
		for (DataSetRow row : set) {
			List<PatientIdentifier> patientIdentifiers = (List<PatientIdentifier>)row.getColumnValue(identifierColumn);
			PatientIdentifier patientIdentifier = patientIdentifiers.get(0);
			String patientIdentifierVal = patientIdentifier != null ? patientIdentifier.getIdentifier() : null;
			Assert.assertTrue("Patient identifier '" + patientIdentifierVal + "' not expected", expectedPatientIdentifiers.contains(patientIdentifierVal));
			expectedPatientIdentifiers.remove(patientIdentifierVal);
		}
	}

	/**
	 * Prints report data to the console
	 * @param data the report data
	 * @throws java.io.IOException if error occurs
	 */
	public static void printReport(ReportData data) throws IOException {
		System.out.println("------------ " + data.getDefinition().getName() + " -------------");
		new TsvReportRenderer().render(data, null, System.out);
		System.out.println("-------------------------------");
	}
}
