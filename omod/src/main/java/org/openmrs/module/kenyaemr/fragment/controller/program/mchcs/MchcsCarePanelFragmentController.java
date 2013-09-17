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

package org.openmrs.module.kenyaemr.fragment.controller.program.mchcs;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.lang.String;
import java.util.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for child services care summary
 */
public class MchcsCarePanelFragmentController {

	public void controller(@FragmentParam("patient") Patient patient,
						   @FragmentParam("complete") Boolean complete,
						   FragmentModel model) {

		Map<String, Object> calculations = new HashMap<String, Object>();
		List<Obs> milestones = new ArrayList<Obs>();
		List<Obs> remarks = new ArrayList<Obs>();
		Obs heiOutcomes = null;
		Obs hivExposed = null;
		Obs hivStatus = null;
		String developmentalMilestones = new String();
		String developmentalRemarks = new String();


		EncounterService encounterService = Context.getEncounterService();
		EncounterType checkout_encounterType = encounterService.getEncounterTypeByUuid(Metadata.EncounterType.CHECK_OUT);
		Encounter lastMchcsCheckout = EmrUtils.lastEncounter(patient,checkout_encounterType);
		EncounterType mchcs_enrollment_encounterType = encounterService.getEncounterTypeByUuid(Metadata.EncounterType.MCHCS_ENROLLMENT);
		Encounter lastMchcsEnrollment = EmrUtils.lastEncounter(patient,mchcs_enrollment_encounterType);
		EncounterType mchcs_consultation_encounterType = encounterService.getEncounterTypeByUuid(Metadata.EncounterType.MCHCS_CONSULTATION);
		Encounter lastMchcsConsultation = EmrUtils.lastEncounter(patient,mchcs_consultation_encounterType);




		if (lastMchcsCheckout != null && lastMchcsEnrollment != null) {
			heiOutcomes = EmrUtils.firstObsInEncounter(lastMchcsCheckout, Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION));
			hivExposed =  EmrUtils.firstObsInEncounter(lastMchcsEnrollment, Dictionary.getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS));
			hivStatus =  EmrUtils.firstObsInEncounter(lastMchcsCheckout, Dictionary.getConcept(Dictionary.HIV_STATUS));
		}

		if ((hivExposed != null) && (hivExposed.getValueCoded() != Context.getConceptService().getConceptByUuid(Dictionary.EXPOSURE_TO_HIV))){
			calculations.put("heioutcomes", "Not HIV Exposed");
		}
		else if (heiOutcomes != null && hivExposed != null && hivStatus != null){
			calculations.put("heioutcomes", heiOutcomes.getValueCoded());
			calculations.put("hivStatus",hivStatus.getValueCoded());
		}
		else {
			calculations.put("heioutcomes", "Still in HEI Care");
			calculations.put("hivStatus", "Not Specified");
		}
		if (lastMchcsConsultation != null) {
			 milestones.addAll(EmrUtils.allObsInEncounter(lastMchcsConsultation, Dictionary.getConcept(Dictionary.DEVELOPMENTAL_MILESTONES)));
			if (milestones.size() > 0) {
				for(Obs mile : milestones) {
					developmentalMilestones += mile.getValueCoded().getName() +",";
				}
				calculations.put("milestones", developmentalMilestones);
			}
			else {
				calculations.put("milestones", "Not Specified");
			}
			remarks.addAll(EmrUtils.allObsInEncounter(lastMchcsConsultation, Dictionary.getConcept(Dictionary.REVIEW_OF_SYSTEMS_DEVELOPMENTAL)));
			if (remarks.size() > 0) {
				for(Obs rem : remarks){
					developmentalRemarks +=rem.getValueCoded().getName() +",";
				}
				calculations.put("remarks", developmentalRemarks);
			}
			else {
				calculations.put("remarks", "Not Specified");
			}
		}
		else {
			calculations.put("milestones", "Not Specified");
			calculations.put("remarks", "Not Specified");
		}

		model.addAttribute("calculations", calculations);
	}

}