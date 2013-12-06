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

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.metadata.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.util.OpenmrsUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Miscellaneous utility methods
 */
public class EmrUtils {

	private static MessageDigest md5Digest;

	static {
		try {
			// This is only for diffing values so MD5 is fine
			md5Digest = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Checks whether a date has any time value
	 * @param date the date
	 * @return true if the date has time
	 * @should return true only if date has time
	 */
	public static boolean dateHasTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.HOUR) != 0 || cal.get(Calendar.MINUTE) != 0 || cal.get(Calendar.SECOND) != 0 || cal.get(Calendar.MILLISECOND) != 0;
	}

	/**
	 * Checks if a given date is today
	 * @param date the date
	 * @return true if date is today
	 */
	public static boolean isToday(Date date) {
		return DateUtils.isSameDay(date, new Date());
	}

	/**
	 * Gets the source form for the given visit (may be null)
	 * @param visit the visit
	 * @return source form
	 */
	public static Form getVisitSourceForm(Visit visit) {
		VisitAttributeType sourceAttrType = MetadataUtils.getVisitAttributeType(CommonMetadata._VisitAttributeType.SOURCE_FORM);
		List<VisitAttribute> attrs =  visit.getActiveAttributes(sourceAttrType);
		return attrs.size() > 0 ? (Form) attrs.get(0).getValue() : null;
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
			if (c.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_1_ADULT)) || c.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_1_PEDS))) {
				return 1;
			}
			if (c.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_2_ADULT)) || c.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_2_PEDS))) {
				return 2;
			}
			if (c.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_3_ADULT)) || c.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_3_PEDS))) {
				return 3;
			}
			if (c.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_4_ADULT)) || c.equals(Dictionary.getConcept(Dictionary.WHO_STAGE_4_PEDS))) {
				return 4;
			}
		}
		return null;
	}

	/**
	 * Parses a CSV list of strings, returning all trimmed non-empty values
	 * @param csv the CSV string
	 * @return the concepts
	 */
	public static List<String> parseCsv(String csv) {
		List<String> values = new ArrayList<String>();

		for (String token : csv.split(",")) {
			token = token.trim();

			if (!StringUtils.isEmpty(token)) {
				values.add(token);
			}
		}
		return values;
	}

	/**
	 * Parses a CSV list of concept ids, UUIDs or mappings
	 * @param csv the CSV string
	 * @return the concepts
	 */
	public static List<Concept> parseConcepts(String csv) {
		List<String> identifiers = parseCsv(csv);
		List<Concept> concepts = new ArrayList<Concept>();

		for (String identifier : identifiers) {
			if (StringUtils.isNumeric(identifier)) {
				concepts.add(Context.getConceptService().getConcept(Integer.valueOf(identifier)));
			}
			else {
				concepts.add(Dictionary.getConcept(identifier));
			}
		}
		return concepts;
	}

	/**
	 * Finds the first obs in an encounter with the given concept
	 * @param encounter the encounter
	 * @param concept the obs concept
	 * @return the obs
	 */
	public static Obs firstObsInEncounter(Encounter encounter, Concept concept) {
		for (Obs obs : encounter.getAllObs()) {
			if (obs.getConcept().equals(concept)) {
				return obs;
			}
		}
		return null;
	}

	/**
	 * Finds all obs in an encounter with the given concept
	 * @param encounter the encounter
	 * @param concept the obs concept
	 * @return the obs list
	 */
	public static List<Obs> allObsInEncounter(Encounter encounter, Concept concept) {
		List<Obs> obsList = new ArrayList<Obs>();
		for (Obs obs : encounter.getAllObs()) {
			if (obs.getConcept().equals(concept)) {
				obsList.add(obs);
			}
		}
		return obsList;
	}

	/**
	 * Finds the first obs during a program enrollment with the given concept
	 * @param enrollment the program enrollment
	 * @param concept the obs concept
	 * @return the obs
	 */
	public static Obs firstObsInProgram(PatientProgram enrollment, Concept concept) {
		List<Obs> obss = Context.getObsService().getObservationsByPersonAndConcept(enrollment.getPatient(), concept);
		Collections.reverse(obss); // Obs come desc by date
		for (Obs obs : obss) {
			if (obs.getObsDatetime().compareTo(enrollment.getDateEnrolled()) >= 0 && (enrollment.getDateCompleted() == null || obs.getObsDatetime().compareTo(enrollment.getDateCompleted()) < 0)) {
				return obs;
			}
		}
		return null;
	}

	/**
	 * Finds the last obs with the given person and concept
	 * @param person the person
	 * @param concept the concept
	 * @return the obs
	 */
	public static Obs lastObs(Person person, Concept concept) {
		List<Obs> obss = Context.getObsService().getObservationsByPersonAndConcept(person, concept);
		return obss.size() > 0 ? obss.get(0) : null;
	}

	/**
	 * Finds the last encounter with the given patient and encounter type
	 * @param patient the patient
	 * @param type the encounter type
	 * @return the encounter
	 */
	public static Encounter lastEncounter(Patient patient, EncounterType type) {
		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, null, null, null, Collections.singleton(type), null, null, null, false);
		return encounters.size() > 0 ? encounters.get(encounters.size() - 1) : null;
	}

	/**
	 * Finds the last encounter during a program enrollment with the given encounter type
	 * @param enrollment the program enrollment
	 * @param type the encounter type
	 * @return the encounter
	 */
	public static Encounter lastEncounterInProgram(PatientProgram enrollment, EncounterType type) {
		List<Encounter> encounters = Context.getEncounterService().getEncounters(enrollment.getPatient(), null, enrollment.getDateEnrolled(), enrollment.getDateCompleted(), null, Collections.singleton(type), null, null, null, false);
		return encounters.size() > 0 ? encounters.get(encounters.size() - 1) : null;
	}

	/**
	 * Computes a hash of a set of values
	 * @param values the input values
	 * @return the hash value
	 */
	public static String hash(String... values) {
		StringBuilder sb = new StringBuilder();
		for (String value : values) {
			sb.append(value != null ? value : "xxxxxxxx");
		}

		md5Digest.update(sb.toString().getBytes());
		return Hex.encodeHexString(md5Digest.digest());
	}
}