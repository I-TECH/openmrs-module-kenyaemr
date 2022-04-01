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

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.module.kenyacore.chore.AbstractChore;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Class to correct all the anomalies created by reusing HIV_CONSULTATION on multiple forms.
 */
@Component("kenyaemr.chore.updateNonHivConsultationEncounters")
public class UpdateNonHivConsultationEncounters extends AbstractChore {

	@Autowired
	private EncounterService encounterService;

	@Autowired
	private FormService formService;
	/**
	 * @see org.openmrs.module.kenyacore.chore.AbstractChore#perform(java.io.PrintWriter)
	 */
	@Override
	public void perform(PrintWriter out) {
		Form TREATMENT_PREPARATION = formService.getFormByUuid(HivMetadata._Form.TREATMENT_PREPARATION);
		Form GBV_SCREENING = formService.getFormByUuid(HivMetadata._Form.GBV_SCREENING);
		Form ALCOHOL_AND_DRUGS_SCREENING = formService.getFormByUuid(HivMetadata._Form.ALCOHOL_AND_DRUGS_SCREENING);
		Form ENHANCED_ADHERENCE_SCREENING = formService.getFormByUuid(HivMetadata._Form.ENHANCED_ADHERENCE_SCREENING);

		EncounterType hivConsultationEncounterType = encounterService.getEncounterTypeByUuid(HivMetadata._EncounterType.HIV_CONSULTATION);
		EncounterType gbvScreeningEncounterType = encounterService.getEncounterTypeByUuid(HivMetadata._EncounterType.GENDER_BASED_VIOLENCE);
		EncounterType alcoholScreeningEncounterType = encounterService.getEncounterTypeByUuid(HivMetadata._EncounterType.ALCOHOL_AND_DRUGS_ABUSE);
		EncounterType artPreparationEncounterType = encounterService.getEncounterTypeByUuid(HivMetadata._EncounterType.ART_PREPARATION);
		EncounterType enhancedAdherenceEncounterType = encounterService.getEncounterTypeByUuid(HivMetadata._EncounterType.ENHANCED_ADHERENCE);

		//find a list of encounter per HIV_CONSULTATION encounterType
		List<Encounter> ea_encounters = encounterService.getEncounters(null, null, null, null, Arrays.asList(ENHANCED_ADHERENCE_SCREENING), Arrays.asList(hivConsultationEncounterType), null, null, null, false);
		List<Encounter> gbv_encounters = encounterService.getEncounters(null, null, null, null, Arrays.asList(GBV_SCREENING), Arrays.asList(hivConsultationEncounterType), null, null, null, false);
		List<Encounter> artPrep_encounters = encounterService.getEncounters(null, null, null, null, Arrays.asList(TREATMENT_PREPARATION), Arrays.asList(hivConsultationEncounterType), null, null, null, false);
		List<Encounter> alcohol_encounters = encounterService.getEncounters(null, null, null, null, Arrays.asList(ALCOHOL_AND_DRUGS_SCREENING), Arrays.asList(hivConsultationEncounterType), null, null, null, false);
		int count = 0;
		for (Encounter ea_encounter : ea_encounters) {
				ea_encounter.setEncounterType(enhancedAdherenceEncounterType);
				count++;
		}
		for (Encounter gbv_encounter : gbv_encounters) {
				gbv_encounter.setEncounterType(gbvScreeningEncounterType);
				count++;
		}
		for (Encounter artPrep_encounter : artPrep_encounters) {
				artPrep_encounter.setEncounterType(artPreparationEncounterType);
				count++;
		}
		for (Encounter alcohol_encounter : alcohol_encounters) {
				alcohol_encounter.setEncounterType(alcoholScreeningEncounterType);
				count++;
		}
		out.println("Updated mismatched hiv_consultation encounter types");
	}
}
