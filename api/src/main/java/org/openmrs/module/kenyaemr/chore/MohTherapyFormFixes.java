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
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
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
 * Class to correct all the anomalies created by moh257 therapy form on cumulative ever on art.
 */
@Component("kenyaemr.chore.mohTherapyFormFixes")
public class MohTherapyFormFixes extends AbstractChore {
	@Autowired
	private ObsService obsService;

	@Autowired
	private EncounterService encounterService;

	@Autowired
	private FormService formService;
	/**
	 * @see org.openmrs.module.kenyacore.chore.AbstractChore#perform(java.io.PrintWriter)
	 */
	@Override
	public void perform(PrintWriter out) {
		Form MOH_257_THERAPY_ENCOUNTER_FORM = formService.getFormByUuid(HivMetadata._Form.MOH_257_ARV_THERAPY);
		Concept concept = Dictionary.getConcept(Dictionary.ANTIRETROVIRAL_TREATMENT_START_DATE);
		EncounterType encounterType = encounterService.getEncounterTypeByUuid(HivMetadata._EncounterType.HIV_ENROLLMENT);

		//find a list of encounter per MOH_257_THERAPY_ENCOUNTER_FORM and encounterType
		List<Encounter> encounters = encounterService.getEncounters(null, null, null, null, Arrays.asList(MOH_257_THERAPY_ENCOUNTER_FORM), Arrays.asList(encounterType), null, null, null, false);
		int count = 0;

		// list of all obs
		Set<Obs> obsList;
		for (Encounter encounter : encounters) {
			obsList = encounter.getAllObs(false);
			for(Obs obs : obsList){
				if(obs.getConcept().equals(concept)){
					obs.setVoided(true);
					obs.setVoidedBy(Context.getAuthenticatedUser());
					obs.setVoidReason("Wrong Concept mapping corrected");
					obs.setDateVoided(new Date());

					count++;
				}
			}
		}
		out.print("Matching patients corrected are  "+count);
	}
}
