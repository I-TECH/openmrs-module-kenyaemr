/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.form.velocity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.DiscontinuationVelocityCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.GreenCardVelocityCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.StablePatientsCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.ipt.OnIptProgramCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.PatientDueForTbProgramEnrollmentCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.PatientInTbProgramCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
/**
 * Velocity functions for adding logic to HTML forms
 */
public class EmrVelocityFunctions {

	private FormEntrySession session;
	protected static final Log log = LogFactory.getLog(EmrVelocityFunctions.class);
	/**
	 * Constructs a new functions provider
	 * @param session the form entry session
	 */
	public EmrVelocityFunctions(FormEntrySession session) {
		this.session = session;
	}

	/**
	 * Checks whether the patient has HIV identifier
	 * @return true if patient has such an identifier
	 */
	public boolean hasHivUniquePatientNumber() {
		if (session.getPatient() == null) {
			return false;
		} else {
			PatientIdentifierType pit = MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER);
			return session.getPatient().getPatientIdentifier(pit) != null;
		}
	}
	/**
	 * Fetches a concept from its identifier
	 * @param conceptIdentifier the concept identifier
	 * @return the concept
	 * @throws org.openmrs.module.metadatadeploy.MissingMetadataException if no such concept exists
	 */
	public Concept getConcept(String conceptIdentifier) {
		return Dictionary.getConcept(conceptIdentifier);
	}

	/**
	 * Checks whether the patient is stable
	 * @return true if patient is stable
	 */

	public Boolean patientIsStable() {

		CalculationResult stablePatient = EmrCalculationUtils.evaluateForPatient(StablePatientsCalculation.class, null,session.getPatient());
		return 	(Boolean) stablePatient.getValue();


	}
	/**
	 * Checks whether the patient is current on ART
	 * @return true if patient is current on ART
	 *
	 * */

	public Boolean currentInArt() {

		CalculationResult patientCurrentInART = EmrCalculationUtils.evaluateForPatient(OnArtCalculation.class, null,session.getPatient());
		return 	(Boolean) patientCurrentInART.getValue();

	}
	/**
	 * Checks whether the patient started ART today
	 * @return true if patient started ART today
	 *
	 * */

//	public Boolean startedArtToday() {
//
//		CalculationResult currentStartDate = EmrCalculationUtils.evaluateForPatient(CurrentARTStartDateCalculation.class, null,session.getPatient());
//
//		return 	(Boolean) patientStartedARTtoday.getValue();
//
//	}
	/**
	 * Checks whether the patient in TB program
	 * @return true if patient is enrolled in TB program
	 *
	 * */

	public Boolean patientInTbProgram() {

		CalculationResult patientEnrolledInTbProgram = EmrCalculationUtils.evaluateForPatient(PatientInTbProgramCalculation.class, null,session.getPatient());
		return 	(Boolean) patientEnrolledInTbProgram.getValue();

	}
	/**
	 * Checks whether the patient is eligible to be enrolled in TB program
	 * @return true if patient is eligible to be enrolled in TB program
	 *
	 * */

	public Boolean patientDueForTbProgramEnrollment() {

		CalculationResult patientEligibleForEnrollmentTbProgram = EmrCalculationUtils.evaluateForPatient(PatientDueForTbProgramEnrollmentCalculation.class, null,session.getPatient());
		return 	(Boolean) patientEligibleForEnrollmentTbProgram.getValue();

	}
	/**
	 * Checks whether the patient in IPT program
	 * @return true if patient is enrolled in TB program
	 *
	 * */

	public Boolean currentInIPT() {

		CalculationResult patientEnrolledInIPTProgram = EmrCalculationUtils.evaluateForPatient(OnIptProgramCalculation.class, null,session.getPatient());
		return 	(Boolean) patientEnrolledInIPTProgram.getValue();

	}
	/**
	 * Checks whether the patient in IPT program
	 * @return true if patient is enrolled in TB program
	 *
	 * */

	public String GreenCardVelocityCalculation() {

		CalculationResult greenCardVelocity = EmrCalculationUtils.evaluateForPatient(GreenCardVelocityCalculation.class, null,session.getPatient());
		return 	(String) greenCardVelocity.getValue();


	}

	public String DiscontinuationVelocityCalculation() {

		CalculationResult discontinuationVelocity = EmrCalculationUtils.evaluateForPatient(DiscontinuationVelocityCalculation.class, null,session.getPatient());
		return 	(String) discontinuationVelocity.getValue();


	}
	/**
		 * Fetches a global property value by property name
		 * @param name the property name
		 * @return the global property value
		 */
	public String getGlobalProperty(String name) {
		return Context.getAdministrationService().getGlobalProperty(name);
	}

	/**
	 * Gets all of the obs with the given concept for the current patient
	 * @param conceptIdentifier the concept identifier
	 * @return the list of obs
	 */
	public String location() {
		AdministrationService administrationService = org.openmrs.api.context.Context.getAdministrationService();
		GlobalProperty globalProperty = administrationService.getGlobalPropertyObject("kenyaemr.defaultLocation");
		if (globalProperty.getValue() != null) {
			return ((Location) globalProperty.getValue()).getName();
		}
		return "Unknown Location";
	}
	public List<Obs> allObs(String conceptIdentifier) {
		if (session.getPatient() == null)
			return new ArrayList<Obs>();

		Patient p = session.getPatient();
		if (p == null)
			return new ArrayList<Obs>();
		else
			return Context.getObsService().getObservationsByPersonAndConcept(p, getConcept(conceptIdentifier));
	}

	/**
	 * Gets the latest obs with the given concept for the current patient
	 * @param conceptIdentifier the concept identifier
	 * @return the most recent obs
	 */
	public Obs latestObs(String conceptIdentifier) {
		List<Obs> obs = allObs(conceptIdentifier);
		if (obs == null || obs.isEmpty())
			return null;
		else
			return obs.get(0);
	}

	/**
	 * Gets the earliest obs with the given concept for the current patient
	 * @param conceptIdentifier the concept identifier
	 * @return the earliest obs
	 */
	public Obs earliestObs(String conceptIdentifier) {
		List<Obs> obs = allObs(conceptIdentifier);
		if (obs == null || obs.isEmpty())
			return null;
		else
			return obs.get(obs.size() - 1);
	}

	/**
	 * Looks for an obs on the same calendar day as today, that is not in the same encounter being edited (if any)
	 * @param conceptIdentifier the obs's concept id, mapping or UUID
	 * @return the obs
	 */
	public Obs obsToday(String conceptIdentifier) {
		Encounter toSkip = session.getEncounter();
		List<Person> p = Collections.singletonList((Person) session.getPatient());
		Concept concept = Dictionary.getConcept(conceptIdentifier);
		Date startOfDay = DateUtil.getStartOfDay(new Date());
		List<Obs> candidates = Context.getObsService().getObservations(p, null, Collections.singletonList(concept), null, null, null, null, null, null, startOfDay, null, false);
		for (Obs candidate : candidates) {
			if (toSkip == null || candidate.getEncounter() == null || !candidate.getEncounter().equals(toSkip)) {
				return candidate;
			}
		}
		return null;
	}
}