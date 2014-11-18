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
