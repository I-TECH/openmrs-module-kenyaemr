/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.util.EmrUiUtils;
import org.openmrs.ui.framework.fragment.FragmentContext;
import org.openmrs.ui.framework.fragment.FragmentModelConfigurator;
import org.openmrs.ui.framework.page.PageContext;
import org.openmrs.ui.framework.page.PageModelConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Prepares the page model and fragment models for all requests
 *
 * Page models will always contain the following attributes:
 *  - patient (loaded from visit, patientId request parameter, or null if neither specified)
 *  - visit (loaded from visitId request parameter, patient active visit, or null if not specified)
 *
 * This class should not throw an APIAuthenticationException but should always save nulls if objects can't be resolved.
 * In this way the page interceptor can handle the authentication check and make a suitable redirect to the login page.
 */
@Component
public class EmrModelConfigurator implements PageModelConfigurator, FragmentModelConfigurator {

	@Autowired
	private EmrUiUtils kenyaEmrUiUtils;

	@Autowired
	private PatientService patientService;

	@Autowired
	private VisitService visitService;

	@Autowired
	private EncounterService encounterService;

	@Override
	public void configureModel(PageContext pageContext) {
		String patientId = pageContext.getRequest().getRequest().getParameter("patientId");
		String visitId = pageContext.getRequest().getRequest().getParameter("visitId");
		String encounterId = pageContext.getRequest().getRequest().getParameter("encounterId");

		Patient currentPatient = null;
		Visit currentVisit = null, activeVisit = null;

		// Look for a current patient
		if (!StringUtils.isEmpty(patientId)) {
			currentPatient = patientFromParam(patientId);
		}

		// Look for a current visit
		if (!StringUtils.isEmpty(visitId)) {
			currentVisit = visitFromParam(visitId);

			if (currentVisit != null) {
				// We can infer patient from current visit
				if (currentPatient == null) {
					currentPatient = currentVisit.getPatient();
				}

				if (!currentPatient.equals(currentVisit.getPatient())) {
					throw new RuntimeException("Mismatch between patient and visit request parameters");
				}
			}
		}

		// If we have a patient, we can look for an active visit
		if (currentPatient != null) {
			activeVisit = getActiveVisit(currentPatient);
		}

		// If we have an encounter we can use it's visit and patient
		if (!StringUtils.isEmpty(encounterId)) {
			Encounter currentEncounter = encounterFromParam(encounterId);
			if (currentEncounter != null) {
				currentPatient = currentEncounter.getPatient();
				currentVisit = currentEncounter.getVisit();
			}
		}

		pageContext.getModel().addAttribute(EmrWebConstants.MODEL_ATTR_CURRENT_PATIENT, currentPatient);
		pageContext.getModel().addAttribute(EmrWebConstants.MODEL_ATTR_CURRENT_VISIT, currentVisit);
		pageContext.getModel().addAttribute(EmrWebConstants.MODEL_ATTR_ACTIVE_VISIT, activeVisit);

		pageContext.getModel().addAttribute("kenyaEmrUi", kenyaEmrUiUtils);
	}

	@Override
	public void configureModel(FragmentContext fragmentContext) {
		fragmentContext.getModel().addAttribute("kenyaEmrUi", kenyaEmrUiUtils);
	}

	/**
	 * Using this instead of the string to patient converter in UIFR as it isn't accessible during testing and we
	 * don't want to throw a APIAuthenticationException
	 * @param id the request parameter value
	 * @return the patient
	 */
	protected Patient patientFromParam(String id) {
		if (StringUtils.isEmpty(id)) {
			return null;
		}
		try {
			return patientService.getPatient(Integer.valueOf(id));
		}
		catch (APIAuthenticationException ex) {
			return null; // Swallow API authentication exceptions
		}
	}

	/**
	 * Using this instead of the string to visit converter as we don't want to throw a APIAuthenticationException
	 * @param id the request parameter value
	 * @return the visit
	 */
	protected Visit visitFromParam(String id) {
		if (StringUtils.isEmpty(id)) {
			return null;
		}
		try {
			return visitService.getVisit(Integer.valueOf(id));
		}
		catch (APIAuthenticationException ex) {
			return null; // Swallow API authentication exceptions
		}
	}

	/**
	 * Using this instead of the string to encounter converter as we don't want to throw a APIAuthenticationException
	 * @param id the request parameter value
	 * @return the visit
	 */
	protected Encounter encounterFromParam(String id) {
		if (StringUtils.isEmpty(id)) {
			return null;
		}
		try {
			return encounterService.getEncounter(Integer.valueOf(id));
		}
		catch (APIAuthenticationException ex) {
			return null; // Swallow API authentication exceptions
		}
	}

	/**
	 * Gets the active visit for the given patient
	 * @param patient the patient
	 * @return the active visit
	 */
	protected Visit getActiveVisit(Patient patient) {
		try {
			List<Visit> activeVisits = Context.getVisitService().getActiveVisitsByPatient(patient);
			return activeVisits.size() > 0 ? activeVisits.get(0) : null;
		}
		catch (APIAuthenticationException ex) {
			return null; // Swallow API authentication exceptions
		}
	}
}