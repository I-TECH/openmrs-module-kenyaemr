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

package org.openmrs.module.kenyaemr.page.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.openmrs.ui.framework.Link;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller for relationship edit page
 */
@SharedPage({EmrConstants.APP_REGISTRATION, EmrConstants.APP_INTAKE, EmrConstants.APP_CLINICIAN})
public class FamilyAndPartnerTestingPageController {

	protected static final Log log = LogFactory.getLog(FamilyAndPartnerTestingPageController.class);
	PatientService patientService = Context.getPatientService();
	PersonService personService = Context.getPersonService();
	EncounterService encounterService = Context.getEncounterService();
	ObsService obsService = Context.getObsService();
	ConceptService conceptService = Context.getConceptService();
	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public void controller(@RequestParam(value="patientId") Patient patient,
						   @RequestParam("returnUrl") String returnUrl,
						   @SpringBean KenyaUiUtils kenyaUi,
						   UiUtils ui,
						   PageRequest pageRequest,
						   PageModel model) {



		// Get all relationships as simple objects
        // patient id, name, sex, age, relation, test date, test result, enrolled, art number, initiated, status

		String familyHistoryGroupingConcept = "160593AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

        // list of direct relations
		/*	Mother, Father, Sibling, Child, Spouse, Partner, Co-Wife */
		List<SimpleObject> enrolledRelationships = new ArrayList<SimpleObject>();
		List<SimpleObject> hivNegativeRelationships = new ArrayList<SimpleObject>();
		List<SimpleObject> otherConctacts = new ArrayList<SimpleObject>();
		String HIV_FAMILY_HISTORY = "7efa0ee0-6617-4cd7-8310-9f95dfee7a82";
		String FAMILY_TESTING_FORM_FOR_NEGATIVE_CLIENTS = "62846fae-8d0b-4202-827e-8b6ffd30e587";
		String FAMILY_AND_PARTNER_TESTING = "975ae894-7660-4224-b777-468c2e710a2a";
		Integer nextTestingDateConcept = 164400;
		Integer HIVTestResultConcept = 159427;
		Integer date_enrolled_in_care = 160555;
		Integer date_confirmed_positive = 160554;
		String HIV_ENROLLMENT_ENCOUNTER = "de78a6be-bfc5-4634-adc3-5f1a280455cc";


		List<RelationshipType> directRelationships = Arrays.asList(
				Context.getPersonService().getRelationshipTypeByUuid("8d91a01c-c2cc-11de-8d13-0010c6dffd0f"), // sibling
				Context.getPersonService().getRelationshipTypeByUuid("8d91a210-c2cc-11de-8d13-0010c6dffd0f"), // parent-child
				Context.getPersonService().getRelationshipTypeByUuid("d6895098-5d8d-11e3-94ee-b35a4132a5e3"), // spouse
				Context.getPersonService().getRelationshipTypeByUuid("007b765f-6725-4ae9-afee-9966302bace4"), // partner
				Context.getPersonService().getRelationshipTypeByUuid("2ac0d501-eadc-4624-b982-563c70035d46") // co-wife
		);

		// list contacts on family history form
		List<Encounter> familyHistoryEncounters = encounterService.getEncounters(
				patient,
				null,
				null,
				null,
				Arrays.asList(Context.getFormService().getFormByUuid(HIV_FAMILY_HISTORY)),
				null,
				null,
				null,
				null,
				false
		);


		// construct object for each contact
		if (familyHistoryEncounters.size() > 0) {
			List<Obs> obs = obsService.getObservations(
					Arrays.asList(Context.getPersonService().getPerson(patient.getPersonId())),
					familyHistoryEncounters,
					Arrays.asList(conceptService.getConceptByUuid(familyHistoryGroupingConcept)),
					null,
					null,
					null,
					Arrays.asList("obsId"),
					null,
					null,
					null,
					null,
					false
			);
			for(Obs o: obs) {
				if (extractFamilyAndPartnerTestingRows(o.getGroupMembers()) != null) {
					otherConctacts.add(extractFamilyAndPartnerTestingRows(o.getGroupMembers()));
				}
			}
		}

		for (Relationship relationship : Context.getPersonService().getRelationshipsByPerson(patient)) {

			// Filter only direct relationships
			if(!directRelationships.contains(relationship.getRelationshipType())) {
				continue;
			}
			Person person = null;
			String type = null;
			String age = null;
			PatientIdentifier UPN = null;
			Boolean alive = null;

			if (patient.equals(relationship.getPersonA())) {
				person = relationship.getPersonB();
				type = relationship.getRelationshipType().getbIsToA();

			}
			else if (patient.equals(relationship.getPersonB())) {
				person = relationship.getPersonA();
				type = relationship.getRelationshipType().getaIsToB();
			}

			String genderCode = person.getGender().toLowerCase();
			String linkUrl, linkIcon;
			age = new StringBuilder().append(person.getAge()).append(" Years").toString();
			PatientIdentifierType pit = patientService.getPatientIdentifierTypeByUuid(Metadata.IdentifierType.UNIQUE_PATIENT_NUMBER);
			List<PatientIdentifier> identifierList = patientService.getPatientIdentifiers(null, Arrays.asList(pit), null, Arrays.asList(patientService.getPatient(person.getId())),null);
			if (identifierList.size() > 0) {
				UPN = identifierList.get(0);
			}

			alive = person.isDead();


			if (person.isPatient()) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("patientId", person.getId());
				params.put("appId", "kenyaemr.medicalEncounter");
				params.put("returnUrl", returnUrl);
				linkUrl = ui.pageLink(pageRequest.getProviderName(), pageRequest.getPageName(), params);
				linkIcon = ui.resourceLink("kenyaui", "images/glyphs/patient_" + genderCode + ".png");
			}
			else {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("personId", person.getId());
                params.put("appId", "kenyaemr.medicalEncounter");
                params.put("returnUrl", returnUrl);
				linkUrl = ui.pageLink(EmrConstants.MODULE_ID, "admin/editAccount", params);
				linkIcon = ui.resourceLink("kenyaui", "images/glyphs/person_" + genderCode + ".png");
			}

			Link link = new Link(kenyaUi.formatPersonName(person), linkUrl, linkIcon);

			if(UPN != null) { // for patient enrolled in HIV
				String dateConfirmed = null;
				String dateEnrolled = null;
				Set<Obs> enrollmentObs = null;
				String first_encounter_date = null;

				Encounter initialEnrollment = firstEncounter(patientService.getPatient(person.getId()), encounterService.getEncounterTypeByUuid(HIV_ENROLLMENT_ENCOUNTER));

				// Extract last test information for negative patients
				if (initialEnrollment != null) {
					enrollmentObs = initialEnrollment.getAllObs();

					first_encounter_date = DATE_FORMAT.format(initialEnrollment.getEncounterDatetime());
					for(Obs o: enrollmentObs) {
						if (o.getConcept().getConceptId().equals(date_enrolled_in_care) ) {
							dateEnrolled = DATE_FORMAT.format(o.getValueDate());
						} else if (o.getConcept().getConceptId().equals(date_confirmed_positive )) {
							dateConfirmed = DATE_FORMAT.format(o.getValueDate());
						}
					}
				}
				enrolledRelationships.add(SimpleObject.create(
						"relationshipId", relationship.getId(),
						"type", type,
						"personLink", link,
						"age" , age ,
						"dateEnrolled" , dateEnrolled != null? dateEnrolled: first_encounter_date ,
						"dateConfirmed" , dateConfirmed != null? dateConfirmed : first_encounter_date ,
						"status", alive? "Dead": "Alive",
						"art_no", UPN != null? UPN.toString(): ""
				));
			} else { // this is for hiv negative patients, not enrolled in HIV program

				Encounter lastContactTestingEncounter = lastEncounter(patientService.getPatient(person.getId()), encounterService.getEncounterTypeByUuid(FAMILY_AND_PARTNER_TESTING));

				String lastTestDate = null;
				String nextTestDate = null;
				String lastTestResult = null;
				Set<Obs> testObs = null;

				// Extract last test information for negative patients
				if (lastContactTestingEncounter != null) {
					testObs = lastContactTestingEncounter.getAllObs();

					lastTestDate = DATE_FORMAT.format(lastContactTestingEncounter.getEncounterDatetime());
					for(Obs o: testObs) {
						if (o.getConcept().getConceptId().equals(HIVTestResultConcept) ) {
							lastTestResult = hivStatusConverter(o.getValueCoded());
						} else if (o.getConcept().getConceptId().equals(nextTestingDateConcept )) {
							nextTestDate = DATE_FORMAT.format(o.getValueDate());
						}
					}
				}

				hivNegativeRelationships.add(SimpleObject.create(
						"relationshipId", relationship.getId(),
						"type", type,
						"personLink", link,
						"age" , age,
						"status", alive? "Dead": "Alive",
						"lastTestDate", lastTestDate != null? lastTestDate: "",
						"lastTestResult", lastTestResult != null? lastTestResult: "",
						"nextTestDate", nextTestDate != null? nextTestDate: ""
				));
			}

		}


		model.addAttribute("patient", patient);
		model.addAttribute("enrolledRelationships", enrolledRelationships);
		model.addAttribute("hivNegativeRelationships", hivNegativeRelationships);
		model.addAttribute("otherContacts", otherConctacts);
		model.addAttribute("stats", getTestingStatistics(enrolledRelationships, hivNegativeRelationships, otherConctacts));
		model.addAttribute("returnUrl", returnUrl);
	}

	SimpleObject extractFamilyAndPartnerTestingRows (Set<Obs> obsList) {

		Integer contactConcept = 160750;
		Integer	ageConcept = 160617;
		Integer relationshipConcept = 1560;
		Integer relStatusConcept = 163607;
		Integer baselineHivStatusConcept =1169;
		Integer nextTestingDateConcept = 164400;
		Integer ageUnitConcept = 1732;
		Integer HIVTestResultConcept = 159427;
		Integer InCareConcept = 159811;
		Integer CCCNoConcept = 162053;

		String relType = null;
		Integer age = 0;
		Double artNo = null;
		String baselineStatus = null;
		String relStatus = null;
		String inCare = null;
		String hivTestResult = null;
		String nextTestDate = null;
		String contactName = null;
		String ageUnit = null;


		for(Obs obs:obsList) {

			if (obs.getConcept().getConceptId().equals(contactConcept) ) {
				contactName = obs.getValueText();
			} else if (obs.getConcept().getConceptId().equals(ageConcept )) { // get age
				age = obs.getValueNumeric().intValue();
			} else if (obs.getConcept().getConceptId().equals(baselineHivStatusConcept) ) {
				baselineStatus = hivStatusConverter(obs.getValueCoded());
			} else if (obs.getConcept().getConceptId().equals(relStatusConcept )) { // current HIV status
				relStatus = hivStatusConverter(obs.getValueCoded());
			} else if (obs.getConcept().getConceptId().equals(HIVTestResultConcept )) { // HIV test result
				hivTestResult = hivStatusConverter(obs.getValueCoded());
			} else if (obs.getConcept().getConceptId().equals(nextTestingDateConcept )) {
				nextTestDate = DATE_FORMAT.format(obs.getValueDate());
			} else if (obs.getConcept().getConceptId().equals(CCCNoConcept )) {
				artNo = obs.getValueNumeric();
			} else if (obs.getConcept().getConceptId().equals(InCareConcept) ) {
				inCare = booleanAnswerConverter(obs.getValueCoded());
			} else if (obs.getConcept().getConceptId().equals(relationshipConcept) ) {
				relType = relationshipConverter(obs.getValueCoded());
			} else if (obs.getConcept().getConceptId().equals(relStatusConcept) ) {
				relStatus = statusConverter(obs.getValueCoded());
			} else if (obs.getConcept().getConceptId().equals(ageUnitConcept) ) {
				ageUnit = ageUnitConverter(obs.getValueCoded());
			}
		}
		if(contactName != null) {
			return SimpleObject.create(
					"contact", contactName.toUpperCase(),
					"relType", relType != null ? relType : "",
					"relStatus", relStatus != null ? relStatus : "",
					"age", age != null ? new StringBuilder().append(age).append(" ").append(ageUnit) : "",
					"art_no", artNo != null ? artNo.intValue() : "",
					"baselineStatus", baselineStatus != null ? baselineStatus : "",
					"nextTestDate", nextTestDate != null ? nextTestDate : "",
					"inCare", inCare != null ? inCare : "",
					"testResult", hivTestResult != null ? hivTestResult : ""
			);
		}
		return null;

	}

	String relationshipConverter (Concept key) {
		Map<Concept, String> relationshipList = new HashMap<Concept, String>();
		relationshipList.put(conceptService.getConcept(970), "Mother");
		relationshipList.put(conceptService.getConcept(971), "Father");
		relationshipList.put(conceptService.getConcept(972), "Sibling");
		relationshipList.put(conceptService.getConcept(1528), "Child");
		relationshipList.put(conceptService.getConcept(5617), "Spouse");
		relationshipList.put(conceptService.getConcept(163565), "Partner");
		relationshipList.put(conceptService.getConcept(162221), "Co-Wife");

		return relationshipList.get(key);
	}

	String statusConverter (Concept key) {
		Map<Concept, String> statusStatusList = new HashMap<Concept, String>();
		statusStatusList.put(conceptService.getConcept(159450), "Current");
		statusStatusList.put(conceptService.getConcept(160432), "Deceased");
		statusStatusList.put(conceptService.getConcept(1067), "Unknown");
		return statusStatusList.get(key);
	}

	String hivStatusConverter (Concept key) {
		Map<Concept, String> hivStatusList = new HashMap<Concept, String>();
		hivStatusList.put(conceptService.getConcept(703), "Positive");
		hivStatusList.put(conceptService.getConcept(664), "Negative");
		hivStatusList.put(conceptService.getConcept(1405), "Exposed");
		hivStatusList.put(conceptService.getConcept(1067), "Unknown");
		return hivStatusList.get(key);
	}

	String booleanAnswerConverter (Concept key) {
		Map<Concept, String> booleanAnswerList = new HashMap<Concept, String>();
		booleanAnswerList.put(conceptService.getConcept(1065), "Yes");
		booleanAnswerList.put(conceptService.getConcept(1066), "No");
		booleanAnswerList.put(conceptService.getConcept(1067), "Unknown");
		return booleanAnswerList.get(key);
	}

	String ageUnitConverter (Concept key) {
		Map<Concept, String> ageUnitAnsList = new HashMap<Concept, String>();
		ageUnitAnsList.put(conceptService.getConcept(1734), "Years");
		ageUnitAnsList.put(conceptService.getConcept(1074), "Months");
		return ageUnitAnsList.get(key);
	}

	SimpleObject getTestingStatistics (List<SimpleObject> enrolledPatients, List<SimpleObject> registeredRelationships, List<SimpleObject> externalPatients ) {
		int totalContacts;
		int knownStatus = 0; // tested or known positives
		int positiveContacts = 0; // known positives + newly testing positive
		int linkedPatients = 0; // those with ART number

		//compute total contacts
		totalContacts = enrolledPatients.size() + externalPatients.size() + registeredRelationships.size();

		// compute linked patients
		// add all enrolled contacts to linked patients
		linkedPatients = linkedPatients + enrolledPatients.size();
		for(SimpleObject row: externalPatients) {

			if ((row.get("inCare") != null && row.get("inCare").equals("Yes"))) {
				log.info(" inCare: " + row.get("inCare") + ", totalLinked before: " + linkedPatients);
				linkedPatients++;
				log.info( " totalLinked after: " + linkedPatients);
			}
		}

		/*
			compute known status
		 */

		// include all enrolled patients to known status
		knownStatus = knownStatus + enrolledPatients.size();

		for(SimpleObject row: externalPatients) {
			if ((row.get("baselineStatus") != null && !row.get("baselineStatus").equals("Unknown")) || (row.get("testResult") != null && !row.get("testResult").equals("Unknown"))) {
				knownStatus++;
			}
		}

		for(SimpleObject row: registeredRelationships) {
			if (row.get("lastTestResult") != null && !row.get("lastTestResult").equals("Unknown")) {
				knownStatus++;
			}
		}

		/*
				Positive contacts
		 */
		// add enrolled patients
		positiveContacts = positiveContacts + enrolledPatients.size();

		// add external contacts
		for(SimpleObject row: externalPatients) {
			if ((row.get("baselineStatus") != null && row.get("baselineStatus").equals("Positive")) || (row.get("testResult") != null && row.get("testResult").equals("Positive"))) {
				positiveContacts++;
			}
		}

		// add registered contacts
		for(SimpleObject row: registeredRelationships) {
			if (row.get("lastTestResult") != null && row.get("lastTestResult").equals("Positive")) {
				positiveContacts++;
			}
		}

		return SimpleObject.create(
				"totalContacts", totalContacts,
				"knownPositives", knownStatus,
				"positiveContacts", positiveContacts,
				"linkedPatients", linkedPatients
		);
	}
	/**
	 * Finds the last encounter during the program enrollment with the given encounter type
	 *
	 * @param type the encounter type
	 *
	 * @return the encounter
	 */
	public Encounter lastEncounter(Patient patient, EncounterType type) {
		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, null, null, null, Collections.singleton(type), null, null, null, false);
		return encounters.size() > 0 ? encounters.get(encounters.size() - 1) : null;
	}

	/**
	 * Finds the first encounter during the program enrollment with the given encounter type
	 *
	 * @param type the encounter type
	 *
	 * @return the encounter
	 */
	public Encounter firstEncounter(Patient patient, EncounterType type) {
		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, null, null, null, Collections.singleton(type), null, null, null, false);
		return encounters.size() > 0 ? encounters.get(0) : null;
	}

	/**
	 * Finds the last encounter of a given type entered via a given form.
	 *
	 * @param encounterType the type of encounter
	 * @param form          the form through which the encounter was entered.
	 */
	public Encounter encounterByForm(Patient patient, EncounterType encounterType, Form form) {
		List<Form> forms = null;
		if (form != null) {
			forms = new ArrayList<Form>();
			forms.add(form);
		}
		EncounterService encounterService = Context.getEncounterService();
		List<Encounter> encounters = encounterService.getEncounters
				(
						patient,
						null,
						null,
						null,
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