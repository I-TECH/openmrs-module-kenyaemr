/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.program.mchcs;

import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.kenyacore.calculation.CalculationUtils;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.MchMetadata;
import org.openmrs.module.kenyaemr.wrapper.EncounterWrapper;
import org.openmrs.module.kenyaemr.wrapper.PatientWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.*;

/**
 * Controller for child services care summary
 */
public class MchcsCarePanelFragmentController {

	public void controller(@FragmentParam("patient") Patient patient,
						   @FragmentParam("complete") Boolean complete,
						   FragmentModel model) {

		Map<String, Object> calculations = new HashMap<String, Object>();
		List<Obs> milestones = new ArrayList<Obs>();
		List<Obs> prophylaxis = new ArrayList<Obs>();
		List<Obs> remarks = new ArrayList<Obs>();
		List<Obs> feeding = new ArrayList<Obs>();
		List<Obs> pcr = new ArrayList<Obs>();
		Obs heiOutcomes = null;
		Obs hivExposed = null;
		Obs hivStatus = null;

		PatientWrapper patientWrapper = new PatientWrapper(patient);

		EncounterType hei_completion_encounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHCS_HEI_COMPLETION);
		Encounter lastMchcsHeiCompletion = patientWrapper.lastEncounter(hei_completion_encounterType);
		EncounterType mchcs_enrollment_encounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHCS_ENROLLMENT);
		Encounter lastMchcsEnrollment = patientWrapper.lastEncounter(mchcs_enrollment_encounterType);
		EncounterType mchcs_consultation_encounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHCS_CONSULTATION);
		Encounter lastMchcsConsultation = patientWrapper.lastEncounter(mchcs_consultation_encounterType);
		Encounter heiProphylaxis = patientWrapper.lastEncounter(mchcs_consultation_encounterType);
		Concept pce = Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION_QUALITATIVE);
		CalculationResultMap ret = new CalculationResultMap();
		PatientCalculationContext context = Context.getService(PatientCalculationService.class).createCalculationContext();
		CalculationResultMap pcrObs = Calculations.allObs(Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION_QUALITATIVE), Arrays.asList(patient.getPatientId()), context);

		if (lastMchcsHeiCompletion != null && lastMchcsEnrollment != null) {
			EncounterWrapper heiCompletionWrapper = new EncounterWrapper(lastMchcsHeiCompletion);
			EncounterWrapper mchcsEnrollmentWrapper = new EncounterWrapper(lastMchcsEnrollment);

			heiOutcomes = heiCompletionWrapper.firstObs(Dictionary.getConcept(Dictionary.REASON_FOR_PROGRAM_DISCONTINUATION));
			hivExposed =  mchcsEnrollmentWrapper.firstObs(Dictionary.getConcept(Dictionary.CHILDS_CURRENT_HIV_STATUS));
			hivStatus =  heiCompletionWrapper.firstObs(Dictionary.getConcept(Dictionary.HIV_STATUS));

		}

		if (hivExposed != null && hivExposed.getValueCoded().equals(Dictionary.getConcept(Dictionary.EXPOSURE_TO_HIV)) && heiOutcomes == null) {
			calculations.put("heioutcomes", "Still in HEI Care");
		}
		if (hivExposed != null && hivExposed.getValueCoded().equals(Dictionary.getConcept(Dictionary.NO)) && heiOutcomes == null) {
			calculations.put("heioutcomes", "Not HIV exposed");
		}
		if (hivExposed != null && hivExposed.getValueCoded().equals(Dictionary.getConcept(Dictionary.UNKNOWN)) && heiOutcomes == null) {
			calculations.put("heioutcomes", "Unknown");
		}
		if (heiOutcomes != null && hivExposed != null && hivStatus != null) {
			calculations.put("heioutcomes", heiOutcomes.getValueCoded());
			calculations.put("hivStatus",hivStatus.getValueCoded());
		}
		if (heiOutcomes != null && hivExposed != null && hivStatus == null) {
			calculations.put("heioutcomes", heiOutcomes.getValueCoded());
			calculations.put("hivStatus", "Not Specified");
		}
		if(hivStatus == null){
			calculations.put("hivStatus", "Not Specified");
		}
		if(heiProphylaxis !=null) {
			EncounterWrapper heiProphylaxisWrapper = new EncounterWrapper(heiProphylaxis);
			prophylaxis.addAll(heiProphylaxisWrapper.allObs(Dictionary.getConcept(Dictionary.MEDICATION_ORDERS)));
			if (prophylaxis.size() > 0) {
				calculations.put("prophylaxis", prophylaxis);
			}
			else {
				calculations.put("prophylaxis", "Not Specified");
			}

		}

		if (lastMchcsConsultation != null) {
			EncounterWrapper mchcsConsultationWrapper = new EncounterWrapper(lastMchcsConsultation);

			milestones.addAll(mchcsConsultationWrapper.allObs(Dictionary.getConcept(Dictionary.DEVELOPMENTAL_MILESTONES)));

			if (milestones.size() > 0) {
				calculations.put("milestones", milestones);
			}
			else {
				calculations.put("milestones", "Not Specified");
			}

			remarks.addAll(mchcsConsultationWrapper.allObs(Dictionary.getConcept(Dictionary.REVIEW_OF_SYSTEMS_DEVELOPMENTAL)));

			if (remarks.size() > 0) {
				calculations.put("remarks", remarks);
			}
			else {
				calculations.put("remarks", "Not Specified");
			}
			feeding.addAll(mchcsConsultationWrapper.allObs(Dictionary.getConcept(Dictionary.INFANT_FEEDING_METHOD)));

			if (remarks.size() > 0) {
				calculations.put("feeding", feeding);
			}
			else {
				calculations.put("feeding", "Not Specified");
			}

			ListResult obsResults = (ListResult) pcrObs.get(patient.getPatientId());
			List<Obs> obsListPCR;
			obsListPCR = CalculationUtils.extractResultValues(obsResults);

			if(obsListPCR !=null){

				if(obsListPCR.size() > 0){
					List<SimpleObject> obbListView = new ArrayList<SimpleObject>();
					for (Obs obs:obsListPCR){
						if(obs.getConcept().equals(pce)){
							Order pcrTestOrder = obs.getOrder();
							String orderReason = "";
							if(pcrTestOrder!= null){
								orderReason = pcrTestOrder.getOrderReason().getName().toString();
							}
							Date pcrDate = obs.getObsDatetime();
							String testResults = obs.getValueCoded().getName().toString();
							SimpleObject pcrTests = SimpleObject.create("orderReason", orderReason, "pcrDate", pcrDate, "testResults", testResults);
							obbListView.add(pcrTests);
							calculations.put("obbListView",obbListView);

						}
					}
				}
			}

		}
		else {
			calculations.put("milestones", "Not Specified");
			calculations.put("remarks", "Not Specified");
			calculations.put("feeding", "Not Specified");
			calculations.put("pcr", "Not Specified");
		}

		model.addAttribute("calculations", calculations);
	}

}