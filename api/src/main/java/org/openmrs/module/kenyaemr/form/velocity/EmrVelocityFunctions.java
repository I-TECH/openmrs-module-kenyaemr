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

package org.openmrs.module.kenyaemr.form.velocity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.OnArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.StablePatientsCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.PatientInTbProgramCalculation;
import org.openmrs.module.kenyaemr.calculation.library.tb.PatientInIptProgramCalculation;

import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.api.PatientService;
import org.openmrs.Patient;
/**
 * Velocity functions for adding logic to HTML forms
 */
public class EmrVelocityFunctions {

	private FormEntrySession session;
	protected static final Log log = LogFactory.getLog(StablePatientsCalculation.class);
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


	}/**
	 * Checks whether the patient in TB program
	 * @return true if patient is enrolled in TB program
	 *
	 * */

	public Boolean currentInArt() {

		CalculationResult patientCurrentInART = EmrCalculationUtils.evaluateForPatient(OnArtCalculation.class, null,session.getPatient());
		return 	(Boolean) patientCurrentInART.getValue();


	}
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
	 * Checks whether the patient in TB program
	 * @return true if patient is enrolled in TB program
	 *
	 * */

	public Boolean patientInIPTProgram() {

		CalculationResult patientEnrolledInIPTProgram = EmrCalculationUtils.evaluateForPatient(PatientInIptProgramCalculation.class, null,session.getPatient());
		return 	(Boolean) patientEnrolledInIPTProgram.getValue();


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