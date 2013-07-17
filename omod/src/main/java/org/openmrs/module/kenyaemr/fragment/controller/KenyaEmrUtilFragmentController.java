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

package org.openmrs.module.kenyaemr.fragment.controller;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.module.kenyaemr.KenyaEmrUiUtils;
import org.openmrs.module.kenyaemr.calculation.CalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.regimen.RegimenChange;
import org.openmrs.module.kenyaemr.regimen.RegimenChangeHistory;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Fragment actions generally useful for KenyaEMR
 */
public class KenyaEmrUtilFragmentController {

	protected static final Log log = LogFactory.getLog(KenyaEmrUtilFragmentController.class);

	/**
	 * Checks if current user session is authenticated
	 * @return simple object {authenticated: true/false}
	 */
	public SimpleObject isAuthenticated() {
		return SimpleObject.create("authenticated", Context.isAuthenticated());
	}

	/**
	 * Attempt to authenticate current user session with the given credentials
	 * @param username the username
	 * @param password the password
	 * @return simple object {authenticated: true/false}
	 */
	public SimpleObject authenticate(@RequestParam("username") String username, @RequestParam("password") String password) {
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
	public SimpleObject nextHivUniquePatientNumber(
			@SpringBean KenyaEmr emr,
			@RequestParam(required = false, value = "comment") String comment) {
		if (comment == null) {
			comment = "Kenya EMR UI";
		}
		String id = emr.getIdentifierManager().getNextHivUniquePatientNumber(comment);
		return SimpleObject.create("value", id);
	}

	/**
	 * Voids the given visit
	 * @param visit the visit
	 * @param reason the reason for voiding
	 * @return the simplified visit
	 */
	public SimpleObject voidVisit(@RequestParam("visitId") Visit visit, @RequestParam("reason") String reason, @SpringBean KenyaEmrUiUtils kenyaEmrUiUtils, UiUtils ui) {
		visit.setVoided(true);
		visit.setVoidedBy(Context.getAuthenticatedUser());
		visit.setVoidReason(reason);
		Context.getVisitService().saveVisit(visit);
		return ui.convert(visit, SimpleObject.class);
	}

	/**
	 * Gets the duration since patient started ART
	 * @param patient the patient
	 * @param now the current time reference
	 * @return the regimen and duration
	 */
	public SimpleObject currentArvRegimen(@RequestParam("patientId") Patient patient, @RequestParam("now") Date now, @SpringBean KenyaEmr emr, @SpringBean KenyaEmrUiUtils kenyaEmrUi, @SpringBean KenyaUiUtils kenyaUi, UiUtils ui) {
		Concept arvs = emr.getRegimenManager().getMasterSetConcept("ARV");
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
		CalculationResult result = CalculationUtils.evaluateForPatient(InitialArtStartDateCalculation.class, null, patient.getPatientId());
		Date artStartDate = result != null ? (Date) result.getValue() : null;

		return SimpleObject.create("duration", artStartDate != null ? kenyaUi.formatInterval(artStartDate, now) : null);
	}

	/**
	 * Gets the patient's age on the given date
	 * @param patient the patient
	 * @param now the current time reference
	 * @return
	 */
	public SimpleObject age(@RequestParam("patientId") Patient patient, @RequestParam("now") Date now) {
		return SimpleObject.create("age", patient.getAge(now));
	}
}