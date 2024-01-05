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
import org.openmrs.module.kenyaemrorderentry.util.Utils;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.*;

import static org.openmrs.module.kenyaemr.calculation.library.mchcs.NeedsPcrTestCalculation.getAgeInWeeks;

/**
 * Controller for child services care summary
 */
public class MchcsCarePanelFragmentController {

	public void controller(@FragmentParam("patient") Patient patient,
						   @FragmentParam("complete") Boolean complete,
						   FragmentModel model) {

		Map<String, Object> calculations = new HashMap<String, Object>();
		List<Obs> milestones = new ArrayList<Obs>();
		String prophylaxis;
		String feeding;
		List<Obs> remarks = new ArrayList<Obs>();
		String heiOutcomes;
		Integer prophylaxisQuestion = 1282;
		Integer feedingMethodQuestion = 1151;
		Integer heiOutcomesQuestion = 159427;

		PatientWrapper patientWrapper = new PatientWrapper(patient);

		EncounterType mchcs_consultation_encounterType = MetadataUtils.existing(EncounterType.class, MchMetadata._EncounterType.MCHCS_CONSULTATION);
		Encounter lastMchcsConsultation = patientWrapper.lastEncounter(mchcs_consultation_encounterType);

		Concept pcrInitialTest = Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION_QUALITATIVE);
		Concept rapidTest = Dictionary.getConcept(Dictionary.RAPID_HIV_CONFIRMATORY_TEST);
		PatientCalculationContext context = Context.getService(PatientCalculationService.class).createCalculationContext();
		CalculationResultMap pcrObs = Calculations.allObs(Dictionary.getConcept(Dictionary.HIV_DNA_POLYMERASE_CHAIN_REACTION_QUALITATIVE), Arrays.asList(patient.getPatientId()), context);
		CalculationResultMap rapidTestObs = Calculations.allObs(Dictionary.getConcept(Dictionary.RAPID_HIV_CONFIRMATORY_TEST), Arrays.asList(patient.getPatientId()), context);
		Encounter lastHeiCWCFollowupEncounter = Utils.lastEncounter(patient, Context.getEncounterService().getEncounterTypeByUuid(MchMetadata._EncounterType.MCHCS_CONSULTATION));
		Encounter lastHeiEnrollmentEncounter = Utils.lastEncounter(patient, Context.getEncounterService().getEncounterTypeByUuid(MchMetadata._EncounterType.MCHCS_ENROLLMENT));
		Encounter lastHeiOutComeEncounter = Utils.lastEncounter(patient, Context.getEncounterService().getEncounterTypeByUuid(MchMetadata._EncounterType.MCHCS_HEI_COMPLETION));

         if(lastHeiOutComeEncounter !=null){
			 for (Obs obs : lastHeiOutComeEncounter.getAllObs() ){
				 if (obs.getConcept().getConceptId().equals(heiOutcomesQuestion)) {
					 heiOutcomes = obs.getValueCoded().getName().toString();
					 calculations.put("heiOutcomes", heiOutcomes);
				 }
			 }
		 }

		if (lastHeiEnrollmentEncounter != null) {
			for (Obs obs : lastHeiEnrollmentEncounter.getObs()) {
				if (obs.getConcept().getConceptId().equals(prophylaxisQuestion)) {
					Integer heiProphylaxisObsAnswer = obs.getValueCoded().getConceptId();
					if (heiProphylaxisObsAnswer.equals(86663)) {
						prophylaxis = obs.getValueCoded().getName().toString();
						calculations.put("prophylaxis", prophylaxis);
					} else if (heiProphylaxisObsAnswer.equals(80586)) {
						prophylaxis =  obs.getValueCoded().getName().toString();
						calculations.put("prophylaxis", prophylaxis);
					} else if (heiProphylaxisObsAnswer.equals(1652)) {
						prophylaxis =  obs.getValueCoded().getName().toString();
						calculations.put("prophylaxis", prophylaxis);
					} else if (heiProphylaxisObsAnswer.equals(1149)) {
						prophylaxis =  obs.getValueCoded().getName().toString();
						calculations.put("prophylaxis", prophylaxis);
					} else if (heiProphylaxisObsAnswer.equals(1107)) {
						prophylaxis =  obs.getValueCoded().getName().toString();
						calculations.put("prophylaxis", prophylaxis);
					} else {
						calculations.put("prophylaxis", "Not Specified");
					}
				}

			}
		}
		if (lastHeiCWCFollowupEncounter != null) {
			for (Obs obs : lastHeiCWCFollowupEncounter.getObs()) {
				if (obs.getConcept().getConceptId().equals(feedingMethodQuestion)) {
					Integer heiBabyFeedingObsAnswer = obs.getValueCoded().getConceptId();
					if (heiBabyFeedingObsAnswer.equals(5526)) {
						feeding = obs.getValueCoded().getName().toString();
						calculations.put("feeding", feeding);
					} else if (heiBabyFeedingObsAnswer.equals(1595)) {
						feeding = obs.getValueCoded().getName().toString();
						calculations.put("feeding", feeding);
					} else if (heiBabyFeedingObsAnswer.equals(6046)) {
						feeding = obs.getValueCoded().getName().toString();
						calculations.put("feeding", feeding);
					} else {
						calculations.put("feeding", "Not Specified");
					}
				}

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

			ListResult obsResults = (ListResult) pcrObs.get(patient.getPatientId());
			List<Obs> obsListPCR;
			obsListPCR = CalculationUtils.extractResultValues(obsResults);

			if(obsListPCR !=null){
				if(obsListPCR.size() > 0){
					List<SimpleObject> obbListView = new ArrayList<SimpleObject>();
					for (Obs obs:obsListPCR){
						if(obs.getConcept().equals(pcrInitialTest)){
							Order pcrTestOrder = obs.getOrder();
							String orderReason = "";
							if(pcrTestOrder!= null){
								orderReason = pcrTestOrder.getOrderReason()!= null ? pcrTestOrder.getOrderReason().getName().toString() : "";
								Date pcrDate = obs.getObsDatetime();
								String testResults = obs.getValueCoded().getName().toString();
								SimpleObject pcrTests = SimpleObject.create("orderReason", orderReason, "pcrDate", pcrDate, "testResults", testResults);
								Integer ageInWeeks = getAgeInWeeks(patient.getBirthdate(), pcrDate);
								obbListView.add(pcrTests);
								calculations.put("obbListView",obbListView);
								calculations.put("ageInWeeks",ageInWeeks);
							}
						}
					}
				}
			}

			ListResult rapidObsResults = (ListResult) rapidTestObs.get(patient.getPatientId());
			List<Obs> obsListRapidTest;
			obsListRapidTest = CalculationUtils.extractResultValues(rapidObsResults);
			if(obsListRapidTest !=null){
				if(obsListRapidTest.size() > 0){
					List<SimpleObject> rapidTestListView = new ArrayList<SimpleObject>();
					for (Obs obs:obsListRapidTest){
						if(obs.getConcept().equals(rapidTest)){
							Order rapidTestOrder = obs.getOrder();
							String rapidOrderReason = "";
							if(rapidTestOrder!= null){
								rapidOrderReason = rapidTestOrder.getOrderReason() != null ? rapidTestOrder.getOrderReason().getName().toString() : "";
								Date rapidTestDate = obs.getObsDatetime();
								String rapidTestResults = obs.getValueCoded().getName().toString();
								SimpleObject rapidTests = SimpleObject.create("rapidOrderReason", rapidOrderReason, "rapidTestDate", rapidTestDate, "rapidTestResults", rapidTestResults);
								rapidTestListView.add(rapidTests);
								calculations.put("rapidTestListView",rapidTestListView);

							}
						}
					}
				}
			}

		}

		model.addAttribute("calculations", calculations);
	}

}