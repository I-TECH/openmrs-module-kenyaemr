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

package org.openmrs.module.kenyaemr.fragment.controller.patient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.page.controller.FamilyAndPartnerTestingPageController;
import org.openmrs.module.kenyaemr.wrapper.VisitWrapper;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.PrivilegeConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Visit summary fragment
 */
public class CurrentVisitSummaryFragmentController {

	protected static final Log log = LogFactory.getLog(CurrentVisitSummaryFragmentController.class);

	PatientService patientService = Context.getPatientService();
	PersonService personService = Context.getPersonService();
	EncounterService encounterService = Context.getEncounterService();
	ObsService obsService = Context.getObsService();
	ConceptService conceptService = Context.getConceptService();
	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	// triage concepts
	Concept WEIGHT = Dictionary.getConcept(Metadata.Concept.WEIGHT_KG);
	Concept HEIGHT = Dictionary.getConcept(Metadata.Concept.HEIGHT_CM);
	Concept TEMP = Dictionary.getConcept("5088AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	Concept PULSE_RATE = Dictionary.getConcept("5087AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	Concept BP_SYSTOLIC = Dictionary.getConcept("5085AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	Concept BP_DIASTOLIC = Dictionary.getConcept("5086AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	Concept RESPIRATORY_RATE = Dictionary.getConcept("5242AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	Concept OXYGEN_SATURATION = Dictionary.getConcept("5092AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	Concept MUAC = Dictionary.getConcept("1343AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	Concept LMP = Dictionary.getConcept("1427AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");



	
	public void controller(@FragmentParam(value = "visit", required = false) Visit visit, @FragmentParam("patient") Patient patient, FragmentModel model) {
		/**
		 * - date last seen (last 3 visits)
		 - diagnoses
		 - medications
		 - next appointment
		 - Triage
		 */

		// Get vitals recorded during the visit
		if(visit != null) {
			// Get recorded triage
			List<Obs> obs = obsService.getObservations(
					Arrays.asList(Context.getPersonService().getPerson(patient.getPersonId())),
					null,
					Arrays.asList(WEIGHT, HEIGHT, TEMP, PULSE_RATE, BP_SYSTOLIC, BP_DIASTOLIC, RESPIRATORY_RATE, OXYGEN_SATURATION, MUAC, LMP),
					null,
					null,
					null,
					Arrays.asList("obsId"),
					null,
					null,
					visit.getStartDatetime(),
					visit.getStopDatetime(),
					false
			);

			if (obs != null) {

				model.addAttribute("vitals", getVisitVitals(obs));
			}
		} else {
			model.addAttribute("vitals", null);
		}
		// Get the last 5 diagnoses
		/**
		 * Diagnosis concept in blue card and green card: 6042
		 * Grouping concept in green card: 159947
		 *
		 */
		// Limit diagnosis to green card, hiv addendum and moh 257 visit summary
		List<Form> formsCollectingDiagnosis = Arrays.asList(
				Context.getFormService().getFormByUuid("23b4ebbd-29ad-455e-be0e-04aa6bc30798"), // moh 257 visit summary
				Context.getFormService().getFormByUuid("22c68f86-bbf0-49ba-b2d1-23fa7ccf0259"), //green card
				Context.getFormService().getFormByUuid("bd598114-4ef4-47b1-a746-a616180ccfc0") // hiv addendum
		);
		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, null, null, formsCollectingDiagnosis, null, null,false);
		// Get recorded triage
		if(visit != null) {
			List<Obs> diagnosisObs = obsService.getObservations(
					Arrays.asList(Context.getPersonService().getPerson(patient.getPersonId())),
					encounters,
					Arrays.asList(conceptService.getConcept(6042)),
					null,
					null,
					null,
					null,
					5,
					null,
					visit.getStartDatetime(),
					visit.getStopDatetime(),
					false
			);

			if (diagnosisObs != null) {

				model.addAttribute("diagnoses", getDiagnoses(diagnosisObs));
			}

		} else {
			model.addAttribute("diagnoses", null);
		}
		// Get medications
		/**
		 * Grouping concept: 1442
		 * Drug: 1282
		 * Dose: 1443
		 * Duration: 159368
		 * Units: 1732
		 */
		// Limit diagnosis to green card, hiv addendum and moh 257 visit summary
		List<Form> medicationForms = Arrays.asList(
				Context.getFormService().getFormByUuid("23b4ebbd-29ad-455e-be0e-04aa6bc30798"), // moh 257 visit summary
				Context.getFormService().getFormByUuid("d4ff8ad1-19f8-484f-9395-04c755de9a47") // other medications
		);
		List<Encounter> medicationEncounters = Context.getEncounterService().getEncounters(patient, null, null, null, medicationForms, null, null,false);
		// Get recorded medications

		if(visit != null) {
			List<Obs> medicationObs = obsService.getObservations(
					Arrays.asList(Context.getPersonService().getPerson(patient.getPersonId())),
					medicationEncounters,
					Arrays.asList(conceptService.getConcept(1442)),
					null,
					null,
					null,
					null,
					null,
					null,
					visit.getStartDatetime(),
					visit.getStopDatetime(),
					false
			);

			log.info("medications: " + medicationObs);
			List<SimpleObject> medicationList = new ArrayList<SimpleObject>();
			for (Obs o : medicationObs) {
				if (getMedications(o.getGroupMembers()) != null) {
					medicationList.add(getMedications(o.getGroupMembers()));
				}
			}

			if (medicationList != null) {

				model.addAttribute("medication", medicationList);
			}
		} else {
			model.addAttribute("medication", null);
		}

		/**
		 * Get list of recent visits - 6 months ago
		 */
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MONTH, -6);
		List<Visit> recentVisits = Context.getVisitService().getVisits(null,
				Collections.singleton(patient),
				null,
				null,
				now.getTime(),
				null,
				null,
				null,
				null,
				true,
				false
				);
		if(recentVisits != null) {
			model.put("recentVisits", getVisits(recentVisits));
		} else {
			model.put("recentVisits", null);
		}

	}

	private List<SimpleObject> getVisits (List<Visit> visitList ) {
		List<SimpleObject> visits = new ArrayList<SimpleObject>();
		for(Visit v : visitList) {
			if(v.getStopDatetime() == null) {
				visits.add(SimpleObject.create(
						"visitDate", new StringBuilder().append(DATE_FORMAT.format(v.getStartDatetime())).toString(),
						"active", true
				));
			} else {
				visits.add(SimpleObject.create(
						"visitDate", new StringBuilder().append(DATE_FORMAT.format(v.getStartDatetime()))
						.append(" - ").append(DATE_FORMAT.format(v.getStopDatetime())),
						"active", false
				));
			}
		}
		return visits;
	}

	private SimpleObject getMedications (Set<Obs> obsList) {

		Integer drugDuration = 159368;
		Integer	drugDurationUnit = 1732;
		Integer frequency = 160855;
		Integer drug = 1282;

		String durationUnit = null;
		Integer duration = 0;
		String frequencyPrescribed  = null;
		String drugName = null;
		String visit = null;


		for(Obs obs:obsList) {

			if (obs.getConcept().getConceptId().equals(drug) ) {
				drugName = obs.getValueCoded().getName().getName();
				visit = DATE_FORMAT.format(obs.getObsDatetime());
			} else if (obs.getConcept().getConceptId().equals(drugDuration )) {
				duration = obs.getValueNumeric().intValue();
			} else if (obs.getConcept().getConceptId().equals(frequency) ) {
				frequencyPrescribed = durationUnitConverter(obs.getValueCoded());
			} else if (obs.getConcept().getConceptId().equals(drugDurationUnit )) {
				durationUnit = durationConverter(obs.getValueCoded());
			}
		}

		if(drugName == null)
			return null;

		return SimpleObject.create(
				"drug", drugName != null? drugName: "",
				"frequency", frequencyPrescribed != null? frequencyPrescribed: "",
				"duration", duration != null? new StringBuilder().append(duration).append(" ").append(durationUnit): "",
				"visitDate", visit
		);
	}

	private Set<SimpleObject> getDiagnoses (List<Obs> obsList) {
		Set<SimpleObject> diagnosisList = new HashSet<SimpleObject>();
		int diagnosisCounter = 0;
		for(Obs o: obsList) {
			if(o.getValueCoded() != null) {
				diagnosisCounter++;

				StringBuilder diagnosis = new StringBuilder().append(diagnosisCounter).append(".")
						.append(o.getValueCoded().getName().getName())
						.append(" => ").append(DATE_FORMAT.format(o.getObsDatetime()));
				diagnosisList.add(SimpleObject.create(
						"diagnosis", diagnosis.toString()
				));
			}
		}
		return diagnosisList;
	}

	private SimpleObject getVisitVitals(List<Obs> obsList) {
		Double weight = null;
		Double height = null;
		Double temp = null;
		Double pulse = null;
		Double bp_systolic = null;
		Double bp_diastolic = null;
		Double resp_rate = null;
		Double oxygen_saturation = null;
		Double muac = null;
		String lmp = null;

		Map<String, Object> vitalsMap = new HashMap<String, Object>();
		for (Obs obs : obsList) {
			if (obs.getConcept().equals(WEIGHT) ) {
				weight = obs.getValueNumeric();
				if(!vitalsMap.keySet().contains("weight")) {
					vitalsMap.put("weight", weight);
				}
			} else if (obs.getConcept().equals(HEIGHT )) {
				height = obs.getValueNumeric();
				if(!vitalsMap.keySet().contains("height")) {
					vitalsMap.put("height", height);
				}
			} else if (obs.getConcept().equals(TEMP) ) {
				temp = obs.getValueNumeric();
				if(!vitalsMap.keySet().contains("temp")) {
					vitalsMap.put("temp", temp);
				}
			} else if (obs.getConcept().equals(PULSE_RATE )) {
				pulse = obs.getValueNumeric();
				if(!vitalsMap.keySet().contains("pulse")) {
					vitalsMap.put("pulse", pulse.intValue());
				}
			} else if (obs.getConcept().equals(BP_SYSTOLIC )) {
				bp_systolic = obs.getValueNumeric();
				if(!vitalsMap.keySet().contains("bp_systolic")) {
					vitalsMap.put("bp_systolic", bp_systolic.intValue());
				}
			} else if (obs.getConcept().equals(BP_DIASTOLIC )) {
				bp_diastolic = obs.getValueNumeric();
				if(!vitalsMap.keySet().contains("bp_diastolic")) {
					vitalsMap.put("bp_diastolic", bp_diastolic.intValue());
				}
			} else if (obs.getConcept().equals(RESPIRATORY_RATE )) {
				resp_rate = obs.getValueNumeric();
				if(!vitalsMap.keySet().contains("resp_rate")) {
					vitalsMap.put("resp_rate", resp_rate.intValue());
				}
			} else if (obs.getConcept().equals(OXYGEN_SATURATION) ) {
				oxygen_saturation = obs.getValueNumeric();
				if(!vitalsMap.keySet().contains("oxygen_saturation")) {
					vitalsMap.put("oxygen_saturation", oxygen_saturation.intValue());
				}
			} else if (obs.getConcept().equals(MUAC) ) {
				muac = obs.getValueNumeric();
				if(!vitalsMap.keySet().contains("muac")) {
					vitalsMap.put("muac", muac);
				}
			} else if (obs.getConcept().equals(LMP) ) {
				lmp = DATE_FORMAT.format(obs.getValueDate());
				if(!vitalsMap.keySet().contains("lmp")) {
					vitalsMap.put("lmp", lmp);
				}
			}

		}

		if (bp_diastolic != null && bp_systolic != null)
			vitalsMap.put("bp", new StringBuilder().append(bp_systolic.intValue()).append("/").append(bp_diastolic.intValue()));


		return SimpleObject.create(
				"weight", vitalsMap.get("weight") != null? new StringBuilder().append(vitalsMap.get("weight")).append(" Kg"): "",
				"height", vitalsMap.get("height") != null? new StringBuilder().append(vitalsMap.get("height")).append(" cm"): "",
				"temperature", vitalsMap.get("temp") != null? new StringBuilder().append(vitalsMap.get("temp")): "",
				"pulse", vitalsMap.get("pulse") != null? new StringBuilder().append(vitalsMap.get("pulse")).append(" "): "",
				"bp", vitalsMap.get("bp") != null? new StringBuilder().append(vitalsMap.get("bp")).append(" mmHg"): "",
				"resp_rate", vitalsMap.get("resp_rate") != null? new StringBuilder().append(vitalsMap.get("resp_rate")).append(" "): "",
				"oxygen_saturation", vitalsMap.get("oxygen_saturation") != null? new StringBuilder().append(vitalsMap.get("oxygen_saturation")).append(" "): "",
				"muac", vitalsMap.get("muac") != null? new StringBuilder().append(vitalsMap.get("muac")).append(" "): "",
				"lmp", vitalsMap.get("lmp") != null? new StringBuilder().append(vitalsMap.get("lmp")): ""
		);
	}

	String durationConverter (Concept key) {
		Map<Concept, String> ageUnitAnsList = new HashMap<Concept, String>();
		ageUnitAnsList.put(conceptService.getConcept(1822), "Hours");
		ageUnitAnsList.put(conceptService.getConcept(1072), "Days");
		ageUnitAnsList.put(conceptService.getConcept(1073), "Weeks");
		ageUnitAnsList.put(conceptService.getConcept(1734), "Years");
		ageUnitAnsList.put(conceptService.getConcept(1074), "Months");
		return ageUnitAnsList.get(key);
	}

	String durationUnitConverter (Concept key) {
		Map<Concept, String> ageUnitAnsList = new HashMap<Concept, String>();
		ageUnitAnsList.put(conceptService.getConcept(160862), "Once daily");
		ageUnitAnsList.put(conceptService.getConcept(160863), "Once daily at bedtime");
		ageUnitAnsList.put(conceptService.getConcept(160864), "Once daily in the evening");
		ageUnitAnsList.put(conceptService.getConcept(160865), "Once daily in the morning");
		ageUnitAnsList.put(conceptService.getConcept(160858), "Twice daily");
		ageUnitAnsList.put(conceptService.getConcept(160866), "Thrice daily");
		ageUnitAnsList.put(conceptService.getConcept(160870), "Four times daily");
		return ageUnitAnsList.get(key);
	}

}