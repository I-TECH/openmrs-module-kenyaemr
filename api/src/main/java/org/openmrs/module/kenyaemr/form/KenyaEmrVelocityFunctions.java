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
package org.openmrs.module.kenyaemr.form;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.reporting.common.DateUtil;


/**
 *
 */
public class KenyaEmrVelocityFunctions {
	
	FormEntrySession session;
	
	/**
     * @param session
     */
    public KenyaEmrVelocityFunctions(FormEntrySession session) {
	    this.session = session;
    }
    
    public boolean hasHivUniquePatientNumber() {
    	if (session.getPatient() == null) {
    		return false;
    	} else {
    		PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierTypeByUuid(MetadataConstants.UNIQUE_PATIENT_NUMBER_UUID);
    		return session.getPatient().getPatientIdentifier(pit) != null;
    	}
    }

    /**
     * Looks for an obs on the same calendar day as today, that is not in the same encounter being edited (if any)
     * 
     * @param conceptId
     * @return
     */
	public Obs obsToday(Integer conceptId) {
		Encounter toSkip = session.getEncounter();
		List<Person> p = Collections.singletonList((Person) session.getPatient());
		List<Concept> c = Collections.singletonList(Context.getConceptService().getConcept(conceptId));
		Date startOfDay = DateUtil.getStartOfDay(new Date());
		List<Obs> candidates = Context.getObsService().getObservations(p, null, c, null, null, null, null, null, null, startOfDay, null, false);
		for (Obs candidate : candidates) {
			if (toSkip == null || candidate.getEncounter() == null || !candidate.getEncounter().equals(toSkip)) {
				return candidate;
			}
		}
		return null;
	}
	
}
