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
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Relationship;
import org.openmrs.Visit;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.Metadata;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.regimen.RegimenChange;
import org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.kenyaemr.util.EmrUiUtils;
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
	 */
	public SimpleObject clientsBookedOnDate(@RequestParam(value = "appointmentDate") String tca) {

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
}