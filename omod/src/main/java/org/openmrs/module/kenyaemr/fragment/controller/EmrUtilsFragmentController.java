/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.Relationship;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.IPTMetadata;
import org.openmrs.module.kenyaemr.regimen.RegimenChange;
import org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.util.EmrUiUtils;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.AppAction;
import org.openmrs.module.kenyaui.annotation.PublicAction;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Fragment actions generally useful for KenyaEMR
 */
public class EmrUtilsFragmentController {

	protected static final Log log = LogFactory.getLog(EmrUtilsFragmentController.class);

	/**
	 * Checks if current user session is authenticated
	 * @return simple object {authenticated: true/false}
	 */
	@PublicAction
	public SimpleObject isAuthenticated() {
		return SimpleObject.create("authenticated", Context.isAuthenticated());
	}

	/**
	 * Attempt to authenticate current user session with the given credentials
	 * @param username the username
	 * @param password the password
	 * @return simple object {authenticated: true/false}
	 */
	@PublicAction
	public SimpleObject authenticate(@RequestParam(value = "username", required = false) String username,
									 @RequestParam(value = "password", required = false) String password) {
		try {
			Context.authenticate(username, password);
		} catch (ContextAuthenticationException ex) {
			// do nothing
		}
		return isAuthenticated();
	}

	/**
	 * Gets the next HIV patient number from the generator
	 * @param comment the optional comment
	 * @return simple object { value: identifier value }
	 */
	public SimpleObject nextHivUniquePatientNumber(@RequestParam(required = false, value = "comment") String comment) {
		if (comment == null) {
			comment = "KenyaEMR UI";
		}

		String id = Context.getService(KenyaEmrService.class).getNextHivUniquePatientNumber(comment);
		return SimpleObject.create("value", id);
	}
	/**
	 * Checks whether provided identifier(s) is already assigned
	 * @return simple object with statuses for the different identifiers
	 */
	public SimpleObject identifierExists(@RequestParam(value = "heiNumber", required = false) String heiNumber,
										 @RequestParam(value = "upn", required = false) String upn,
										 @RequestParam(value = "cwcNumber", required = false) String cwcNumber,
										 @RequestParam(value = "clinicNumber", required = false) String clinicNumber) {
		boolean heiNoExists = false;
		boolean upnExists = false;
		boolean clinicNoExists = false;
		boolean cwcNoExists = false;
		PatientService patientService = Context.getPatientService();

		if (heiNumber != null && heiNumber != "") {
			List<Patient> patientsFound = patientService.getPatients(null, heiNumber.trim(), Arrays.asList(MetadataUtils.existing(PatientIdentifierType.class, Metadata.IdentifierType.HEI_UNIQUE_NUMBER)), true );
			if (patientsFound.size() > 0)
				heiNoExists = true;
		}

		if (upn != null && upn != "") {
			List<Patient> patientsFound = patientService.getPatients(null, upn.trim(), Arrays.asList(MetadataUtils.existing(PatientIdentifierType.class, Metadata.IdentifierType.UNIQUE_PATIENT_NUMBER)), true );
			if (patientsFound.size() > 0)
				upnExists = true;
		}

		if (clinicNumber != null && clinicNumber != "") {
			List<Patient> patientsFound = patientService.getPatients(null, clinicNumber.trim(), Arrays.asList(MetadataUtils.existing(PatientIdentifierType.class, Metadata.IdentifierType.PATIENT_CLINIC_NUMBER)), true );
			if (patientsFound.size() > 0)
				clinicNoExists = true;
		}

		if (cwcNumber != null && cwcNumber != "") {
			List<Patient> patientsFound = patientService.getPatients(null, cwcNumber.trim(), Arrays.asList(MetadataUtils.existing(PatientIdentifierType.class, Metadata.IdentifierType.CWC_NUMBER)), true );
			if (patientsFound.size() > 0)
				cwcNoExists = true;
		}
		return SimpleObject.create(
				"heiNumberExists", heiNoExists,
				"upnExists",upnExists,
				"clinicNumberExists", clinicNoExists,
				"cwcNoExists", cwcNoExists
				);
	}

	/**
	 * Checks whether provided identifier(s) is already assigned
	 * @return simple object with statuses for the different identifiers
	 * Uses Next appointments for HIV greencard Triage and HIV consultation
	 */
	public SimpleObject clientsBookedForHivConsultationOnDate(@RequestParam(value = "appointmentDate") String tca) {

		System.out.println("Date passed from browser: " + tca);
		String appointmentQuery = "select count(patient_id) as bookings\n" +
				"from (\n" +
				"select\n" +
				"e.patient_id\n" +
				"from encounter e\n" +
				"inner join\n" +
				"(\n" +
				"\tselect encounter_type_id, uuid, name from encounter_type where uuid in('a0034eee-1940-4e35-847f-97537a35d05e','d1059fb9-a079-4feb-a749-eedd709ae542', '465a92f2-baf8-42e9-9612-53064be868e8')\n" +
				") et on et.encounter_type_id=e.encounter_type\n" +
				"inner join obs o on o.encounter_id=e.encounter_id and o.voided=0\n" +
				"\tand o.concept_id in (5096) and date(o.value_datetime) = date('" + tca + "')\n" +
				"where e.voided=0\n" +
				"group by e.patient_id\n" +
				")t;";


		Long bookings = (Long) Context.getAdministrationService().executeSQL(appointmentQuery, true).get(0).get(0);

		return SimpleObject.create(
			"bookingsOnDate", bookings
		);



	}
	/**
	 * Checks whether provided identifier(s) is already assigned
	 * @return simple object with statuses for the different identifiers
	 * Uses Next appointments for MCH consultation and CWC consulation
	 *
	 */
	public SimpleObject clientsBookedForMchConsultationOnDate(@RequestParam(value = "appointmentDate") String tca) {

		System.out.println("Date passed from browser: " + tca);
		String appointmentQuery = "select count(patient_id) as bookings\n" +
				"from (\n" +
				"select\n" +
				"e.patient_id\n" +
				"from encounter e\n" +
				"inner join\n" +
				"(\n" +
				"\tselect encounter_type_id, uuid, name from encounter_type where uuid in('c6d09e05-1f25-4164-8860-9f32c5a02df0','bcc6da85-72f2-4291-b206-789b8186a021')\n" +
				") et on et.encounter_type_id=e.encounter_type\n" +
				"inner join obs o on o.encounter_id=e.encounter_id and o.voided=0\n" +
				"\tand o.concept_id in (5096) and date(o.value_datetime) = date('" + tca + "')\n" +
				"where e.voided=0\n" +
				"group by e.patient_id\n" +
				")t;";


		Long bookings = (Long) Context.getAdministrationService().executeSQL(appointmentQuery, true).get(0).get(0);

		return SimpleObject.create(
				"bookingsOnDate", bookings
		);
	}
/**
 * Checks whether provided identifier(s) is already assigned
 * @return simple object with statuses for the different identifiers
 * Uses last recorded lot number to prepopulate lot numbers in HTS tests
 *
 */
	public SimpleObject lastLotNumberUsedForHTSTesting() {

		String getLastLotNumberQuery = "select\n" +
				"  max(if(o.concept_id=164964,trim(o.value_text),null)) as lot_no,\n" +
				"  max(if(o.concept_id=162502,date(o.value_datetime),null)) as expiry_date\n" +
				"from openmrs.obs o\n" +
				"  inner join openmrs.encounter e on e.encounter_id = o.encounter_id\n" +
				"  inner join openmrs.form f on f.form_id=e.form_id and f.uuid in ('72aa78e0-ee4b-47c3-9073-26f3b9ecc4a7')\n" +
				"where o.concept_id in (164962,164964, 162502) and o.voided=0\n" +
				"group by e.encounter_id ORDER BY e.encounter_id DESC limit 1 ;";

		Long lastLotNumber = (Long) Context.getAdministrationService().executeSQL(getLastLotNumberQuery, true).get(0).get(0);

		return SimpleObject.create(
				"lastLotNumber", lastLotNumber
		);


	}
	/**
	 * Voids the given relationship
	 * @param relationship the relationship
	 * @param reason the reason for voiding
	 * @return the simplified visit
	 */
	public SuccessResult voidRelationship(@RequestParam("relationshipId") Relationship relationship, @RequestParam("reason") String reason) {
		Context.getPersonService().voidRelationship(relationship, reason);
		return new SuccessResult("Relationship voided");
	}

	/**
	 * Voids the given visit
	 * @param visit the visit
	 * @param reason the reason for voiding
	 * @return the simplified visit
	 */
	@AppAction(EmrConstants.APP_CHART)
	public SuccessResult voidVisit(@RequestParam("visitId") Visit visit, @RequestParam("reason") String reason) {
		Context.getVisitService().voidVisit(visit, reason);
		return new SuccessResult("Visit voided");
	}

	/**
	 * Gets the current ARV regimen
	 * @param patient the patient
	 * @param now the current time reference
	 * @return the regimen and duration
	 */
	public SimpleObject currentArvRegimen(@RequestParam("patientId") Patient patient, @RequestParam("now") Date now, @SpringBean RegimenManager regimenManager, @SpringBean EmrUiUtils kenyaEmrUi, @SpringBean KenyaUiUtils kenyaUi, UiUtils ui) {
		Concept arvs = regimenManager.getMasterSetConcept("ARV");
		RegimenChangeHistory history = RegimenChangeHistory.forPatient(patient, arvs);
		RegimenChange current = history.getLastChangeBeforeDate(now);

		return SimpleObject.create(
				"regimen", current != null ? kenyaEmrUi.formatRegimenShort(current.getStarted(), ui) : null,
				"duration", current != null ? kenyaUi.formatInterval(current.getDate(), now) : null
		);
	}

	/**
	 * Gets the duration since patient started ART
	 * @param patient the patient
	 * @param now the current time reference
	 * @return the duration interval
	 */
	public SimpleObject durationSinceStartArt(@RequestParam("patientId") Patient patient, @RequestParam("now") Date now, @SpringBean KenyaUiUtils kenyaUi) {
		CalculationResult result = EmrCalculationUtils.evaluateForPatient(InitialArtStartDateCalculation.class, null, patient);
		Date artStartDate = result != null ? (Date) result.getValue() : null;

		return SimpleObject.create("duration", artStartDate != null ? kenyaUi.formatInterval(artStartDate, now) : null);
	}

	/**
	 * Calculates an estimated birthdate from an age value
	 * @param now the current time reference
	 * @param age the age
	 * @return the ISO8601 formatted birthdate
	 */
	public SimpleObject birthdateFromAge(@RequestParam(value = "age") Integer age,
										 @RequestParam(value = "now", required = false) Date now,
										 @SpringBean KenyaUiUtils kenyaui) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(now != null ? now : new Date());
		cal.add(Calendar.YEAR, -age);
		return SimpleObject.create("birthdate", kenyaui.formatDateParam(cal.getTime()));
	}

	/**
	 * enrolls a patient in IPT program
	 * @param patient to be enrolled
	 * @param enrollmentDate date to be enrolled
	 * @return simple object with patient program id
	 */
	public SimpleObject enrollInIptProgram(@RequestParam("patientId") Patient patient, @RequestParam("enrollmentDate") Date enrollmentDate) {
		PatientProgram iptEnrollment = new PatientProgram();
		ProgramWorkflowService programWorkflowService = Context.getProgramWorkflowService();
		iptEnrollment.setProgram(programWorkflowService.getProgramByUuid(IPTMetadata._Program.IPT));
		iptEnrollment.setPatient(patient);
		iptEnrollment.setDateEnrolled(enrollmentDate);
		iptEnrollment.setLocation(Context.getService(KenyaEmrService.class).getDefaultLocation());
		PatientProgram enrolled = programWorkflowService.savePatientProgram(iptEnrollment);

		// add ipt initiation encounter
		Encounter enc = new Encounter();
		enc.setLocation(Context.getService(KenyaEmrService.class).getDefaultLocation());
		EncounterService encounterService = Context.getEncounterService();
		enc.setEncounterType(encounterService.getEncounterTypeByUuid(IPTMetadata._EncounterType.IPT_INITIATION));
		enc.setEncounterDatetime(enrollmentDate);
		enc.setPatient(patient);
		enc.addProvider(encounterService.getEncounterRole(1), Context.getProviderService().getProvider(1));
		enc.setForm(Context.getFormService().getFormByUuid(IPTMetadata._Form.IPT_INITIATION));
		encounterService.saveEncounter(enc);

		return SimpleObject.create("enrolledInIpt", enrolled != null ? enrolled.getPatientProgramId() : null);

	}

	/**
	 * completes IPT program
	 * @param patient
	 * @param event i.e. discontinued, ltfu, completed, etc
	 * @param completionDate
	 * @return
	 */
	public SimpleObject discontinueIptProgram(@RequestParam("patientId") Patient patient,
										   @RequestParam("event") Concept event,
										   @RequestParam("reason") Concept reason,
										   @RequestParam("action") String action,
										   @RequestParam("completionDate") Date completionDate) {
		ProgramWorkflowService programWorkflowService = Context.getProgramWorkflowService();
		List<PatientProgram> iptProgramEnrollments = programWorkflowService.getPatientPrograms(patient, programWorkflowService.getProgramByUuid(IPTMetadata._Program.IPT), null, null, null, null, false );
		ConceptService conceptService = Context.getConceptService();
		String discontinuationEventConcept = "160433AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		String discontinuationReasonConcept = "1266AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		String actionTakenConcept = "160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

		boolean successful = false;

		PatientProgram lastEnrollment = null;
		if (iptProgramEnrollments != null && iptProgramEnrollments.size() > 0) {
			lastEnrollment = iptProgramEnrollments.get(0);
		}
		if (lastEnrollment != null) {
			lastEnrollment.setDateCompleted(completionDate);
			lastEnrollment.setOutcome(event);
			programWorkflowService.savePatientProgram(lastEnrollment);

			// add outcome encounter

			Encounter enc = new Encounter();
			enc.setLocation(Context.getService(KenyaEmrService.class).getDefaultLocation());
			EncounterService encounterService = Context.getEncounterService();
			enc.setEncounterType(encounterService.getEncounterTypeByUuid(IPTMetadata._EncounterType.IPT_OUTCOME));
			enc.setEncounterDatetime(completionDate);
			enc.setPatient(patient);
			enc.addProvider(encounterService.getEncounterRole(1), Context.getProviderService().getProvider(1));
			enc.setForm(Context.getFormService().getFormByUuid(IPTMetadata._Form.IPT_OUTCOME));

			// build discontinuation observations
			if (event != null) {
				Obs o = new Obs(); // build event obs
				o.setConcept(conceptService.getConcept(discontinuationEventConcept));
				o.setDateCreated(new Date());
				o.setCreator(Context.getAuthenticatedUser());
				o.setLocation(enc.getLocation());
				o.setObsDatetime(enc.getEncounterDatetime());
				o.setPerson(patient);
				o.setValueCoded(event);
				enc.addObs(o);
			}
			if (reason != null) {
				Obs discReason = new Obs(); // build reason obs
				discReason.setConcept(conceptService.getConcept(discontinuationReasonConcept));
				discReason.setDateCreated(new Date());
				discReason.setCreator(Context.getAuthenticatedUser());
				discReason.setLocation(enc.getLocation());
				discReason.setObsDatetime(enc.getEncounterDatetime());
				discReason.setPerson(patient);
				discReason.setValueCoded(reason);
				enc.addObs(discReason);
			}

			if (action != null) {
				Obs discAction = new Obs(); // build reason obs
				discAction.setConcept(conceptService.getConcept(actionTakenConcept));
				discAction.setDateCreated(new Date());
				discAction.setCreator(Context.getAuthenticatedUser());
				discAction.setLocation(enc.getLocation());
				discAction.setObsDatetime(enc.getEncounterDatetime());
				discAction.setPerson(patient);
				discAction.setValueText(action);
				enc.addObs(discAction);
			}
			Encounter discEnc = encounterService.saveEncounter(enc);
			successful = discEnc.getEncounterId() != null ? true : false;

		}
		return SimpleObject.create("discontinueInIpt", successful ? lastEnrollment.getPatientProgramId() : null);

	}

	public SimpleObject addIptFollowup(@RequestParam("patientId") Patient patient, @RequestParam("outcomeConcept") Concept outcome, @RequestParam("encounterDate") Date encounterDate) {
		Encounter enc = new Encounter();
		enc.setLocation(Context.getService(KenyaEmrService.class).getDefaultLocation());
		EncounterService encounterService = Context.getEncounterService();
		enc.setEncounterType(encounterService.getEncounterTypeByUuid(IPTMetadata._EncounterType.IPT_FOLLOWUP));
		enc.setEncounterDatetime(encounterDate);
		enc.setPatient(patient);
		enc.addProvider(encounterService.getEncounterRole(1), Context.getProviderService().getProvider(1));
		enc.setForm(Context.getFormService().getFormByUuid(IPTMetadata._Form.IPT_FOLLOWUP));
		Visit v = getActiveVisit(patient);
		if (v != null) {
			enc.setVisit(v);
			encounterService.saveEncounter(enc);
			return SimpleObject.create("enrolledInIpt", enc != null ? enc.getEncounterId() : null);
		}

		return SimpleObject.create("enrolledInIpt", "There was a problem adding IPT follow-up details");

	}

	/**
	 * Gets the active visit for the given patient
	 * @param patient the patient
	 * @return the active visit
	 */
	protected Visit getActiveVisit(Patient patient) {
		try {
			List<Visit> activeVisits = Context.getVisitService().getActiveVisitsByPatient(patient);
			if (activeVisits.size() > 0) {
				return activeVisits.get(0);
			} else {
				Visit newVisit = new Visit();
				newVisit.setPatient(patient);
				newVisit.setStartDatetime(new Date());
				newVisit.setVisitType(MetadataUtils.existing(VisitType.class, CommonMetadata._VisitType.OUTPATIENT));
				return Context.getVisitService().saveVisit(newVisit);
			}
		}
		catch (APIAuthenticationException ex) {
			return null; // Swallow API authentication exceptions
		}
	}

	private void setEncounterObs(Encounter enc) {

		Integer finalHivTestResultConcept = 159427;
		Integer testTypeConcept = 162084;
		Integer testStrategyConcept = 164956;
		Integer healthProviderConcept = 1473;
		Integer healthFacilityNameConcept = 162724;
		Integer healthProviderIdentifierConcept = 163161;
		// test result
		/*Obs o = new Obs();
		o.setConcept(conceptService.getConcept(finalHivTestResultConcept));
		o.setDateCreated(new Date());
		o.setCreator(Context.getUserService().getUser(1));
		o.setLocation(enc.getLocation());
		o.setObsDatetime(enc.getEncounterDatetime());
		o.setPerson(this.patient);
		o.setValueCoded(hivTest.getResult());

		// test type
		Obs o1 = new Obs();
		o1.setConcept(conceptService.getConcept(testTypeConcept));
		o1.setDateCreated(new Date());
		o1.setCreator(Context.getUserService().getUser(1));
		o1.setLocation(enc.getLocation());
		o1.setObsDatetime(enc.getEncounterDatetime());
		o1.setPerson(this.patient);
		o1.setValueCoded(testTypeConverter(hivTest.getType().trim()));

		// test strategy
		Obs o2 = new Obs();
		o2.setConcept(conceptService.getConcept(testStrategyConcept));
		o2.setDateCreated(new Date());
		o2.setCreator(Context.getUserService().getUser(1));
		o2.setLocation(enc.getLocation());
		o2.setObsDatetime(enc.getEncounterDatetime());
		o2.setPerson(this.patient);
		o2.setValueCoded(hivTest.getStrategy());

		// test provider
		// only do this if provider details is not null

		Obs o3 = new Obs();
		o3.setConcept(conceptService.getConcept(healthProviderConcept));
		o3.setDateCreated(new Date());
		o3.setCreator(Context.getUserService().getUser(1));
		o3.setLocation(enc.getLocation());
		o3.setObsDatetime(enc.getEncounterDatetime());
		o3.setPerson(this.patient);
		o3.setValueText(hivTest.getProviderName().trim());

		// test provider id
		Obs o5 = new Obs();
		o5.setConcept(conceptService.getConcept(healthProviderIdentifierConcept));
		o5.setDateCreated(new Date());
		o5.setCreator(Context.getUserService().getUser(1));
		o5.setLocation(enc.getLocation());
		o5.setObsDatetime(enc.getEncounterDatetime());
		o5.setPerson(this.patient);
		o5.setValueText(hivTest.getProviderId().trim());

		// test facility
		Obs o4 = new Obs();
		o4.setConcept(conceptService.getConcept(healthFacilityNameConcept));
		o4.setDateCreated(new Date());
		o4.setCreator(Context.getUserService().getUser(1));
		o4.setLocation(enc.getLocation());
		o4.setObsDatetime(enc.getEncounterDatetime());
		o4.setPerson(this.patient);
		o4.setValueText(hivTest.getFacility().trim());


		enc.addObs(o);
		enc.addObs(o1);
		enc.addObs(o2);
		enc.addObs(o3);
		enc.addObs(o4);
		enc.addObs(o5);
		encounterService.saveEncounter(enc);*/
	}
}