/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.chore;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.module.kenyacore.chore.AbstractChore;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Clerks forgot to adjust hiv enrollment encounter dates for patients who had already been enrolled in other facilities
 * This affect the transfer in details and the RDE data entry.
 */
@Component("kenyaemr.chore.hivEnrollmentEncounterDateSynched")
public class HivEnrollmentEncounterDateSynched extends AbstractChore {

	@Autowired
	private EncounterService encounterService;

	@Autowired
	private FormService formService;
	/**
	 * @see org.openmrs.module.kenyacore.chore.AbstractChore#perform(java.io.PrintWriter)
	 */

	@Override
	public void perform(PrintWriter out) {

		Form MOH_257_FACE_PAGE_ENCOUNTER_FORM = formService.getFormByUuid(HivMetadata._Form.MOH_257_FACE_PAGE);
		Form HIV_ENROLLMENT_FORM =  formService.getFormByUuid(HivMetadata._Form.HIV_ENROLLMENT);
		Concept concept = Dictionary.getConcept(Dictionary.DATE_ENROLLED_IN_HIV_CARE);
		EncounterType encounterType = encounterService.getEncounterTypeByUuid(HivMetadata._EncounterType.HIV_ENROLLMENT);

		//find a list of encounter per MOH_257_FACE_PAGE_ENCOUNTER_FORM and encounterType
		List<Encounter> encounters = encounterService.getEncounters(null, null, null, null, Arrays.asList(MOH_257_FACE_PAGE_ENCOUNTER_FORM, HIV_ENROLLMENT_FORM), Arrays.asList(encounterType), null, null, null, false);
		int count = 0;
		int found_matching = 0;
		// list of all obs
		Set<Obs> obsList;
		Date encounter_date;
		Date obs_value_date;
		for (Encounter encounter : encounters) {
			encounter_date =  encounter.getEncounterDatetime();
			obsList = encounter.getAllObs(false);
			for(Obs obs : obsList) {
				if (obs.getConcept().equals(concept)) {
					found_matching++;
					obs_value_date = obs.getValueDatetime();
						if(!(encounter_date.equals(obs_value_date))) {
							encounter.setEncounterDatetime(obs.getValueDatetime());
							count++;
						}
				}
			}
		}
		out.println("Matching found "+found_matching+" observations");
		out.println("Adjusted "+count+" encounters to reflect date first enrolled into care");

	}
}
