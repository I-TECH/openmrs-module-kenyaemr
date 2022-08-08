/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.wrapper;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyacore.wrapper.AbstractObjectWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Wrapper class for PatientProgram objects
 */
public class Enrollment extends AbstractObjectWrapper<PatientProgram> {

    /**
     * Creates a PatientProgram wrapper
     *
     * @param target the patient program
     */
    public Enrollment(PatientProgram target) {
        super(target);
    }

    /**
     * Finds the first obs during the program enrollment with the given concept
     *
     * @param concept the obs concept
     *
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
     *
     * @param type the encounter type
     *
     * @return the encounter
     */
    public Encounter lastEncounter(EncounterType type) {
        List<Encounter> encounters = Context.getEncounterService().getEncounters(target.getPatient(), null, target.getDateEnrolled(), target.getDateCompleted(), null, Collections.singleton(type), null, null, null, false);
        return encounters.size() > 0 ? encounters.get(encounters.size() - 1) : null;
    }

    /**
     * Finds the last encounter of a given type entered via a given form.
     *
     * @param encounterType the type of encounter
     * @param form          the form through which the encounter was entered.
     */
    public Encounter encounterByForm(EncounterType encounterType, Form form) {
        List<Form> forms = null;
        if (form != null) {
            forms = new ArrayList<Form>();
            forms.add(form);
        }
        EncounterService encounterService = Context.getEncounterService();
        List<Encounter> encounters = encounterService.getEncounters
                (
                        target.getPatient(),
                        null,
                        target.getDateEnrolled(),
                        target.getDateCompleted(),
                        forms,
                        Collections.singleton(encounterType),
                        null,
                        null,
                        null,
                        false
                );
        return encounters.size() > 0 ? encounters.get(encounters.size() - 1) : null;
    }
}