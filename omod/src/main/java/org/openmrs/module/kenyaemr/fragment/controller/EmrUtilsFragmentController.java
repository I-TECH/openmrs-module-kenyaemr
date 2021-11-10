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
import org.openmrs.Form;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.Relationship;
import org.openmrs.User;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.VisitType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.CoreContext;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyacore.form.FormManager;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
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
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
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
	 * Get patients CCC number
	 * @param patient
	 * @param now
	 * @return ccc number
	 * */

	public String getPatientUniquePatientNumber(@RequestParam(value = "patientId", required = false) Integer patientId) {
		String cccNumber = "";
		PatientService patientService = Context.getPatientService();
		if (patientId != null) {

			    PatientIdentifierType pit = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
				PatientIdentifier cccObject =  patientService.getPatient(patientId).getPatientIdentifier(pit);
				 cccNumber = cccObject.getIdentifier();
				}

			return cccNumber;
	}

	/**
	 * Checks whether provided identifier(s) is already assigned
	 * @return simple object with statuses for the different identifiers
	 * Uses Next appointments for HIV greencard Triage and HIV consultation
	 */
	public SimpleObject clientsBookedForHivConsultationOnDate(@RequestParam(value = "appointmentDate") String tca) {

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


		try {
			Context.addProxyPrivilege(PrivilegeConstants.SQL_LEVEL_ACCESS);
			Long bookings = (Long) Context.getAdministrationService().executeSQL(appointmentQuery, true).get(0).get(0);
			return SimpleObject.create(
					"bookingsOnDate", bookings
			);
		}
		finally {
			Context.removeProxyPrivilege(PrivilegeConstants.SQL_LEVEL_ACCESS);
		}
	}
	/**
	 * Checks whether provided identifier(s) is already assigned
	 * @return simple object with statuses for the different identifiers
	 * Uses Next appointments for MCH consultation and CWC consulation
	 *
	 */
	public SimpleObject clientsBookedForMchConsultationOnDate(@RequestParam(value = "appointmentDate") String tca) {

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

		try {
			Context.addProxyPrivilege(PrivilegeConstants.SQL_LEVEL_ACCESS);
			Long bookings = (Long) Context.getAdministrationService().executeSQL(appointmentQuery, true).get(0).get(0);
			return SimpleObject.create(
					"bookingsOnDate", bookings
			);
		}
		finally {
			Context.removeProxyPrivilege(PrivilegeConstants.SQL_LEVEL_ACCESS);
		}
	}
/**
 * Checks whether provided identifier(s) is already assigned
 * @return simple object with statuses for the different identifiers
 * Uses last recorded lot number to prepopulate lot numbers in HTS tests
 *
 */
public SimpleObject lastLotNumberUsedForHTSTesting(@RequestParam(value = "kitName", required = false) String kitName) {

	Integer currentUserId = Context.getAuthenticatedUser().getPerson().getPersonId();

	String getLastLotNumberQuery = "select\n" +
			"max(if(o.concept_id=164964,trim(o.value_text),null)) as lot_no\n" +
			"from\n" +
			"obs o\n" +
			"inner join encounter e on e.encounter_id = o.encounter_id\n" +
			"inner join form f on f.form_id=e.form_id and f.uuid in\n" +
			"('402dc5d7-46da-42d4-b2be-f43ea4ad87b0','b08471f6-0892-4bf7-ab2b-bf79797b8ea4','e8f98494-af35-4bb8-9fc7-c409c8fed843','72aa78e0-ee4b-47c3-9073-26f3b9ecc4a7','496c7cc3-0eea-4e84-a04c-2292949e2f7f')\n" +
			"where o.concept_id in (164962,164964, 162502) and e.creator = '" + currentUserId + "' and o.voided=0\n" +
			"group by e.encounter_id,o.obs_group_id\n" +
			"Having max(if(o.concept_id=164962,trim(o.value_coded),null)) = '" + kitName + "'\n" +
			"ORDER BY e.encounter_id  DESC limit 1 ;";

	  try {
		       Context.addProxyPrivilege(PrivilegeConstants.SQL_LEVEL_ACCESS);
			   String lastLotNumber = Context.getAdministrationService().executeSQL(getLastLotNumberQuery, true).get(0).get(0).toString();
		    if(lastLotNumber != null || lastLotNumber != "") {
			  return SimpleObject.create(
					  "lastLotNumber", lastLotNumber
			  );
			}
		} finally {
			Context.removeProxyPrivilege(PrivilegeConstants.SQL_LEVEL_ACCESS);
		}
	return SimpleObject.create("No previous lot numbers have been saved");
}

	/**
	 * Checks whether provided identifier(s) is already assigned
	 * @return simple object with statuses for the different identifiers
	 * Uses last recorded lot Expiry date to prepopulate lot numbers in HTS tests
	 *
	 */
	public SimpleObject lastLotExpiryDateUsedForHTSTesting(@RequestParam(value = "kitName", required = false) String kitName) {

		Integer currentUserId = Context.getAuthenticatedUser().getPerson().getPersonId();

		String getLastLotExpiryDateQuery = "select\n" +
				"max(if(o.concept_id=162502,date(o.value_datetime),null)) as expiry_date\n" +
				"from\n" +
				"obs o\n" +
				"inner join encounter e on e.encounter_id = o.encounter_id\n" +
				"inner join form f on f.form_id=e.form_id and f.uuid in\n" +
				"('402dc5d7-46da-42d4-b2be-f43ea4ad87b0','b08471f6-0892-4bf7-ab2b-bf79797b8ea4','e8f98494-af35-4bb8-9fc7-c409c8fed843','72aa78e0-ee4b-47c3-9073-26f3b9ecc4a7','496c7cc3-0eea-4e84-a04c-2292949e2f7f')\n" +
				"where o.concept_id in (164962,164964, 162502) and e.creator = '" + currentUserId + "' and o.voided=0\n" +
				"group by e.encounter_id, o.obs_group_id\n" +
				"Having max(if(o.concept_id=164962,trim(o.value_coded),null)) = '" + kitName + "'\n" +
				"ORDER BY e.encounter_id  DESC limit 1 ;";

		try {
			Context.addProxyPrivilege(PrivilegeConstants.SQL_LEVEL_ACCESS);
			Object lastLotExpiryDate =null;
			 lastLotExpiryDate = Context.getAdministrationService().executeSQL(getLastLotExpiryDateQuery, true).get(0).get(0);

			if(lastLotExpiryDate.toString() != null || lastLotExpiryDate.toString() != "") {
				return SimpleObject.create(
						"lastLotExpiryDate", lastLotExpiryDate
				);
			}
		}
		finally {
			Context.removeProxyPrivilege(PrivilegeConstants.SQL_LEVEL_ACCESS);
		}
		return SimpleObject.create("No previous lot numbers have been saved");
	}
	/**
	 * Checks whether provided identifier(s) is already assigned
	 * @return simple object with statuses for the different identifiers
	 * Uses last recorded lot Expiry date for test 2 to prepopulate lot numbers in HTS tests
	 *
	 */
	public SimpleObject lastLotExpiryDate2UsedForHTSTesting(@RequestParam(value = "kitName", required = false) String kitName) {

		Integer currentUserId = Context.getAuthenticatedUser().getPerson().getPersonId();

		String getLastLotExpiryDateQuery = "select\n" +
				"max(if(o.concept_id=162501,date(o.value_datetime),null)) as expiry_date\n" +
				"from\n" +
				"obs o\n" +
				"inner join encounter e on e.encounter_id = o.encounter_id\n" +
				"inner join form f on f.form_id=e.form_id and f.uuid in\n" +
				"('402dc5d7-46da-42d4-b2be-f43ea4ad87b0','b08471f6-0892-4bf7-ab2b-bf79797b8ea4','e8f98494-af35-4bb8-9fc7-c409c8fed843','72aa78e0-ee4b-47c3-9073-26f3b9ecc4a7','496c7cc3-0eea-4e84-a04c-2292949e2f7f')\n" +
				"where o.concept_id in (164962,164964, 162501) and e.creator = '" + currentUserId + "' and o.voided=0\n" +
				"group by e.encounter_id, o.obs_group_id\n" +
				"Having max(if(o.concept_id=164962,trim(o.value_coded),null)) = '" + kitName + "'\n" +
				"ORDER BY e.encounter_id  DESC limit 1 ;";

		try {
			Context.addProxyPrivilege(PrivilegeConstants.SQL_LEVEL_ACCESS);
			String lastLotExpiryDate = Context.getAdministrationService().executeSQL(getLastLotExpiryDateQuery, true).get(0).get(0).toString();
			if(lastLotExpiryDate != null || lastLotExpiryDate != "") {
				return SimpleObject.create(
						"lastLotExpiryDate", lastLotExpiryDate
				);
			}
		}
		finally {
			Context.removeProxyPrivilege(PrivilegeConstants.SQL_LEVEL_ACCESS);
		}
		return SimpleObject.create("No previous lot numbers have been saved");
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
	public SimpleObject enrollInIptProgram(@RequestParam("patientId") Patient patient,
										   @RequestParam("enrollmentDate") Date enrollmentDate,
										   @RequestParam("indicationForIpt") Concept indicationForIpt,
										   @RequestParam("userId") User loggedInUser) {

		// check if there is no active enrollment
		ProgramWorkflowService programWorkflowService = Context.getProgramWorkflowService();
		List<PatientProgram> iptProgramEnrollments = programWorkflowService.getPatientPrograms(patient, programWorkflowService.getProgramByUuid(IPTMetadata._Program.IPT), null, null, null, null, false );
		ConceptService conceptService = Context.getConceptService();
		String indicationForIptConcept = "162276AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

		PatientProgram lastEnrollment = null;
		if (iptProgramEnrollments != null && iptProgramEnrollments.size() > 0) {
			lastEnrollment = iptProgramEnrollments.get(iptProgramEnrollments.size() - 1);
		}
		if (lastEnrollment != null && lastEnrollment.getActive()) {
			return SimpleObject.create("status", "Error","message","The patient is already initiated in the IPT program");
		}

		PatientProgram iptEnrollment = new PatientProgram();
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

		if (indicationForIpt != null) {
			Obs iptIndication = new Obs(); // build indication for tpt obs
			iptIndication.setConcept(conceptService.getConceptByUuid(indicationForIptConcept));
			iptIndication.setDateCreated(new Date());
			iptIndication.setCreator(loggedInUser);
			iptIndication.setLocation(enc.getLocation());
			iptIndication.setObsDatetime(enc.getEncounterDatetime());
			iptIndication.setPerson(patient);
			iptIndication.setValueCoded(indicationForIpt);
			enc.addObs(iptIndication);
		}
		try {
			encounterService.saveEncounter(enc);
			return SimpleObject.create("status", "Success","message","The patient has been successfully initiated in IPT");
		} catch (Exception e) {
			return SimpleObject.create("status", "Error","message","There was an error initiating the patient in IPT");
		}

	}

	/**
	 * completes IPT program
	 * @param patient
	 * @param reason i.e. discontinued, ltfu, completed, etc
	 * @param completionDate
	 * @return
	 */
	public SimpleObject discontinueIptProgram(@RequestParam("patientId") Patient patient,
										   @RequestParam("reason") Concept reason,
										   @RequestParam("action") String action,
										   @RequestParam("userId") User loggedInUser,
										   @RequestParam("completionDate") Date completionDate) {

		ProgramWorkflowService programWorkflowService = Context.getProgramWorkflowService();
		List<PatientProgram> iptProgramEnrollments = programWorkflowService.getPatientPrograms(patient, programWorkflowService.getProgramByUuid(IPTMetadata._Program.IPT), null, null, null, null, false );
		ConceptService conceptService = Context.getConceptService();
		String discontinuationReasonConcept = "161555AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		String actionTakenConcept = "160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

		PatientProgram lastEnrollment = null;
		if (iptProgramEnrollments != null && iptProgramEnrollments.size() > 0) {
			lastEnrollment = iptProgramEnrollments.get(iptProgramEnrollments.size() - 1);
		}
		if (lastEnrollment != null && lastEnrollment.getActive()) {
			try {
				lastEnrollment.setDateCompleted(completionDate);
				lastEnrollment.setOutcome(reason);

				// add outcome encounter

				Encounter enc = new Encounter();
				enc.setLocation(Context.getService(KenyaEmrService.class).getDefaultLocation());
				EncounterService encounterService = Context.getEncounterService();
				enc.setEncounterType(encounterService.getEncounterTypeByUuid(IPTMetadata._EncounterType.IPT_OUTCOME));
				enc.setEncounterDatetime(completionDate);
				enc.setPatient(patient);
				enc.setCreator(loggedInUser);
				enc.setForm(Context.getFormService().getFormByUuid(IPTMetadata._Form.IPT_OUTCOME));

				// build discontinuation observations

				if (reason != null) {
					Obs discReason = new Obs(); // build reason obs
					discReason.setConcept(conceptService.getConceptByUuid(discontinuationReasonConcept));
					discReason.setDateCreated(new Date());
					discReason.setCreator(loggedInUser);
					discReason.setLocation(enc.getLocation());
					discReason.setObsDatetime(enc.getEncounterDatetime());
					discReason.setPerson(patient);
					discReason.setValueCoded(reason);
					enc.addObs(discReason);
				}

				if (action != null) {
					Obs discAction = new Obs(); // build reason obs
					discAction.setConcept(conceptService.getConceptByUuid(actionTakenConcept));
					discAction.setDateCreated(new Date());
					discAction.setCreator(loggedInUser);
					discAction.setLocation(enc.getLocation());
					discAction.setObsDatetime(enc.getEncounterDatetime());
					discAction.setPerson(patient);
					discAction.setValueText(action);
					enc.addObs(discAction);
				}

				programWorkflowService.savePatientProgram(lastEnrollment);
				encounterService.saveEncounter(enc);

				return SimpleObject.create("status", "Success","message","The patient has been successfully discontinued in IPT");
			} catch (Exception e) {
				return SimpleObject.create("status", "Error","message","There was an error while discontinuing the patient in IPT");
			}

		}

		return SimpleObject.create("status", "Error","message","The patient is not initiated in IPT");

	}

	public SimpleObject addIptFollowup(@RequestParam("patientId") Patient patient,
									   @RequestParam("userId") User loggedInUser,
									   @RequestParam("encounterDate") Date encounterDate,
									   @RequestParam("iptDueDate") Date iptDueDate,
									   @RequestParam("iptCollectionDate") Date iptCollectionDate,
									   @RequestParam("iptHepatoxicity") Concept hepatoxicity,
									   @RequestParam("iptNeuropathy") Concept neuropathy,
									   @RequestParam("iptRash") Concept rash,
									   @RequestParam("iptAdherence") Concept adherence,
									   @RequestParam(value = "iptActionTaken", required = false) String actionTaken) {

		Integer iptDueDateConcept = 164073;
		Integer iptCollectionDateConcept = 164074;
		String hepatoxicityConcept = "159098AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		String neuropathyConcept = "118983AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		String rashConcept = "512AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		String adherenceConcept = "164075AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		Integer actionTakenConcept = 160632;
		ConceptService conceptService = Context.getConceptService();

		Encounter enc = new Encounter();
		enc.setLocation(Context.getService(KenyaEmrService.class).getDefaultLocation());
		EncounterService encounterService = Context.getEncounterService();
		enc.setEncounterType(encounterService.getEncounterTypeByUuid(IPTMetadata._EncounterType.IPT_FOLLOWUP));
		enc.setEncounterDatetime(encounterDate);
		enc.setPatient(patient);
		enc.setForm(Context.getFormService().getFormByUuid(IPTMetadata._Form.IPT_FOLLOWUP));
		enc.setCreator(loggedInUser);

		// set obs

		if (iptDueDate != null) {
			Obs iptDueDateObs = new Obs(); // build reason obs
			iptDueDateObs.setConcept(conceptService.getConcept(iptDueDateConcept));
			iptDueDateObs.setDateCreated(new Date());
			iptDueDateObs.setCreator(loggedInUser);
			iptDueDateObs.setLocation(enc.getLocation());
			iptDueDateObs.setObsDatetime(enc.getEncounterDatetime());
			iptDueDateObs.setPerson(patient);
			iptDueDateObs.setValueDatetime(iptDueDate);
			enc.addObs(iptDueDateObs);
		}

		if (iptCollectionDate != null) {
			Obs iptCollectionDateObs = new Obs(); // build reason obs
			iptCollectionDateObs.setConcept(conceptService.getConcept(iptCollectionDateConcept));
			iptCollectionDateObs.setDateCreated(new Date());
			iptCollectionDateObs.setCreator(loggedInUser);
			iptCollectionDateObs.setLocation(enc.getLocation());
			iptCollectionDateObs.setObsDatetime(enc.getEncounterDatetime());
			iptCollectionDateObs.setPerson(patient);
			iptCollectionDateObs.setValueDatetime(iptCollectionDate);
			enc.addObs(iptCollectionDateObs);
		}

		if (hepatoxicity != null) {
			Obs hepatoxicityObs = new Obs(); // build hepatoxicity obs
			hepatoxicityObs.setConcept(conceptService.getConceptByUuid(hepatoxicityConcept));
			hepatoxicityObs.setDateCreated(new Date());
			hepatoxicityObs.setCreator(loggedInUser);
			hepatoxicityObs.setLocation(enc.getLocation());
			hepatoxicityObs.setObsDatetime(enc.getEncounterDatetime());
			hepatoxicityObs.setPerson(patient);
			hepatoxicityObs.setValueCoded(hepatoxicity);
			enc.addObs(hepatoxicityObs);
		}

		if (neuropathy != null) {
			Obs neuropathyObs = new Obs(); // build hepatoxicity obs
			neuropathyObs.setConcept(conceptService.getConceptByUuid(neuropathyConcept));
			neuropathyObs.setDateCreated(new Date());
			neuropathyObs.setCreator(loggedInUser);
			neuropathyObs.setLocation(enc.getLocation());
			neuropathyObs.setObsDatetime(enc.getEncounterDatetime());
			neuropathyObs.setPerson(patient);
			neuropathyObs.setValueCoded(neuropathy);
			enc.addObs(neuropathyObs);
		}

		if (rash != null) {
			Obs rashObs = new Obs(); // build hepatoxicity obs
			rashObs.setConcept(conceptService.getConceptByUuid(rashConcept));
			rashObs.setDateCreated(new Date());
			rashObs.setCreator(loggedInUser);
			rashObs.setLocation(enc.getLocation());
			rashObs.setObsDatetime(enc.getEncounterDatetime());
			rashObs.setPerson(patient);
			rashObs.setValueCoded(rash);
			enc.addObs(rashObs);
		}

		if (adherence != null) {
			Obs adherenceObs = new Obs(); // build adherence obs
			adherenceObs.setConcept(conceptService.getConceptByUuid(adherenceConcept));
			adherenceObs.setDateCreated(new Date());
			adherenceObs.setCreator(loggedInUser);
			adherenceObs.setLocation(enc.getLocation());
			adherenceObs.setObsDatetime(enc.getEncounterDatetime());
			adherenceObs.setPerson(patient);
			adherenceObs.setValueCoded(adherence);
			enc.addObs(adherenceObs);
		}

		if (actionTaken != null && actionTaken !="") {
			Obs actionTakenObs = new Obs(); // build action taken obs
			actionTakenObs.setConcept(conceptService.getConcept(actionTakenConcept));
			actionTakenObs.setDateCreated(new Date());
			actionTakenObs.setCreator(loggedInUser);
			actionTakenObs.setLocation(enc.getLocation());
			actionTakenObs.setObsDatetime(enc.getEncounterDatetime());
			actionTakenObs.setPerson(patient);
			actionTakenObs.setValueText(actionTaken);
			enc.addObs(actionTakenObs);
		}

		assignToVisit(enc, Context.getVisitService().getVisitTypeByUuid(CommonMetadata._VisitType.OUTPATIENT));
		try{
			encounterService.saveEncounter(enc);
			return SimpleObject.create("status", "Success","message","IPT followup details saved successfully");
		} catch (Exception e) {
			return SimpleObject.create("status", "Error","message","There was an error updating IPT followup details");
		}
	}

	/**
	 * Does the actual assignment of the encounter to a visit
	 * @param encounter the encounter
	 * @param newVisitType the type of the new visit if one is created
	 */
	protected void assignToVisit(Encounter encounter, VisitType newVisitType) {
		// Do nothing if the encounter already belongs to a visit and can't be moved
		if (encounter.getVisit() != null && newVisitType == null) {
			return;
		}

		// Try using an existing visit
		if (!useExistingVisit(encounter)) {
			if (newVisitType != null) {
				useNewVisit(encounter, newVisitType, encounter.getForm());
			}
		}
	}

	/**
	 * Uses an existing a visit for the given encounter
	 * @param encounter the encounter
	 * @return true if a suitable visit was found
	 */
	protected boolean useExistingVisit(Encounter encounter) {
		// If encounter has time, then we need an exact fit for an existing visit
		if (EmrUtils.dateHasTime(encounter.getEncounterDatetime())) {
			List<Visit> visits = Context.getVisitService().getVisits(null, Collections.singletonList(encounter.getPatient()), null, null, null,
					encounter.getEncounterDatetime(), null, null, null, true, false);

			for (Visit visit : visits) {
				// Skip visits which ended before the encounter date
				if (visit.getStopDatetime() != null && visit.getStopDatetime().before(encounter.getEncounterDatetime())) {
					continue;
				}

				if (checkLocations(visit, encounter)) {
					setVisitOfEncounter(visit, encounter);
					return true;
				}
			}
		}
		// If encounter does not have time, we can move it to fit any visit that day
		else {
			List<Visit> existingVisitsOnDay = Context.getService(KenyaEmrService.class).getVisitsByPatientAndDay(encounter.getPatient(), encounter.getEncounterDatetime());
			if (existingVisitsOnDay.size() > 0) {
				Visit visit = existingVisitsOnDay.get(0);

				if (checkLocations(visit, encounter)) {
					setVisitOfEncounter(visit, encounter);

					// Adjust encounter start if its before visit start
					if (encounter.getEncounterDatetime().before(visit.getStartDatetime())) {
						encounter.setEncounterDatetime(visit.getStartDatetime());
					}

					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Uses a new visit for the given encounter
	 * @param encounter the encounter
	 * @param type the visit type
	 * @param sourceForm the source form
	 */
	protected static void useNewVisit(Encounter encounter, VisitType type, Form sourceForm) {
		Visit visit = new Visit();
		visit.setStartDatetime(OpenmrsUtil.firstSecondOfDay(encounter.getEncounterDatetime()));
		visit.setStopDatetime(OpenmrsUtil.getLastMomentOfDay(encounter.getEncounterDatetime()));
		visit.setLocation(encounter.getLocation());
		visit.setPatient(encounter.getPatient());
		visit.setVisitType(type);

		VisitAttribute sourceAttr = new VisitAttribute();
		sourceAttr.setAttributeType(MetadataUtils.existing(VisitAttributeType.class, CommonMetadata._VisitAttributeType.SOURCE_FORM));
		sourceAttr.setOwner(visit);
		sourceAttr.setValue(sourceForm);
		visit.addAttribute(sourceAttr);

		Context.getVisitService().saveVisit(visit);

		setVisitOfEncounter(visit, encounter);
	}

	/**
	 * Gets an auto-create visit type if there is one for the form used to create the encounter
	 * @param encounter the encounter
	 * @return the visit type
	 */
	protected static VisitType getAutoCreateVisitType(Encounter encounter) {
		if (encounter.getForm() != null) {
			FormManager formManager = CoreContext.getInstance().getManager(FormManager.class);

			FormDescriptor fd = formManager.getFormDescriptor(encounter.getForm());

			if (fd != null && fd.getAutoCreateVisitTypeUuid() != null) {
				return MetadataUtils.existing(VisitType.class, fd.getAutoCreateVisitTypeUuid());
			}
		}
		return null;
	}

	/**
	 * Convenience method to check whether the location of a visit and an encounter are compatible
	 * @param visit the visit
	 * @param encounter the encounter
	 * @return true if locations won't conflict
	 */
	protected static boolean checkLocations(Visit visit, Encounter encounter) {
		return visit.getLocation() == null || Location.isInHierarchy(encounter.getLocation(), visit.getLocation());
	}

	/**
	 * Sets the visit of an encounter, updating the both the old visit and the new visit. This is used rather than just
	 * encounter.setVisit(...) so that we don't have to reload the visit objects to update their set of encounters
	 * @param visit the visit
	 * @param encounter the encounter
	 */
	protected static void setVisitOfEncounter(Visit visit, Encounter encounter) {
		// Remove from old visit
		if (encounter.getVisit() != null) {
			encounter.getVisit().getEncounters().remove(encounter);
		}

		// Set to new visit
		encounter.setVisit(visit);

		if (visit != null) {
			visit.addEncounter(encounter);
		}
	}
}
