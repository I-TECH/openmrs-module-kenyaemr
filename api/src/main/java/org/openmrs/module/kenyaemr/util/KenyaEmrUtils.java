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

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.kenyaemr.KenyaEmrConstants;

import java.util.*;

/**
 * Miscellaneous utility methods
 */
public class KenyaEmrUtils {

	/**
	 * Gets the module version
	 * @return the version
	 */
	public static String getModuleVersion() {
		return ModuleFactory.getModuleById(KenyaEmrConstants.MODULE_ID).getVersion();
	}

	/**
	 * Gets the module build properties
	 * @return the build properties map
	 */
	public static Map<String, String> getModuleBuildProperties() {
		return (Map<String, String>)ContextProvider.getApplicationContext().getBean("kenyaEmrBuildProperties");
	}

	/**
	 * Clears the time portion of a date object
	 * @param date the date
	 * @return the date with no time portion
	 * @should clear time information from date
	 */
	public static Date dateStartOfDay(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(Calendar.AM_PM, 0);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * Add days to an existing date
	 * @param date the date
	 * @param days the number of days to add (negative to subtract days)
	 * @return the new date
	 * @should shift the date by the number of days
	 */
	public static Date dateAddDays(Date date, int days) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	/**
	 * Fetches a list of concepts from a collection of concepts or concept identifiers
	 * @param conceptsOrIds the collection of concepts or concept identifiers
	 * @return the list of concepts
	 * @throws IllegalArgumentException if item in list is not a concept, and Integer or a String
	 * @throws NumberFormatException if a String identifier is not a valid integer
	 * @should fetch from concepts, integers or strings
	 * @should throw exception for non concepts, integers or strings
	 */
	public static List<Concept> fetchConcepts(Collection<?> conceptsOrIds) {
		List<Concept> concepts = new ArrayList<Concept>();
		for (Object o : conceptsOrIds) {
			if (o instanceof Concept) {
				concepts.add((Concept) o);
			}
			else if (o instanceof Integer) {
				concepts.add(Context.getConceptService().getConcept((Integer) o));
			}
			else if (o instanceof String) {
				concepts.add(Context.getConceptService().getConcept(Integer.valueOf(o.toString())));
			}
			else {
				throw new IllegalArgumentException("Must be a concept, and Integer or a String");
			}
		}
		return concepts;
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
}