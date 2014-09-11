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

package org.openmrs.module.kenyaemr.wrapper;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.wrapper.AbstractObjectWrapper;

import java.util.Collections;
import java.util.List;

/**
 * Wrapper class for PatientProgram objects
 */
public class Enrollment extends AbstractObjectWrapper<PatientProgram> {

	/**
	 * Creates a PatientProgram wrapper
	 * @param target the patient program
	 */
	public Enrollment(PatientProgram target) {
		super(target);
	}

	/**
	 * Finds the first obs during the program enrollment with the given concept
	 * @param concept the obs concept
	 * @return the obs
	 */
	public Obs firstObs(Concept concept) {
		List<Obs> obss = Context.getObsService().getObservationsByPersonAndConcept(target.getPatient(), concept);
		Collections.reverse(obss); // Obs come desc by date
		for (Obs obs : obss) {
			if (obs.getObsDatetime().compareTo(target.getDateEnrolled()) >= 0 && (target.getDateCompleted() == null || obs.getObsDatetime().compareTo(target.getDateCompleted()) < 0)) {
				return obs;
			}
		}
		return null;
	}

	/**
	 * Finds the last encounter during the program enrollment with the given encounter type
	 * @param type the encounter type
	 * @return the encounter
	 */
	public Encounter lastEncounter(EncounterType type) {
		List<Encounter> encounters = Context.getEncounterService().getEncounters(target.getPatient(), null, target.getDateEnrolled(), target.getDateCompleted(), null, Collections.singleton(type), null, null, null, false);
		return encounters.size() > 0 ? encounters.get(encounters.size() - 1) : null;
	}
}