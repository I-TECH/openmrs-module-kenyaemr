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

package org.openmrs.module.kenyaemr;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.converter.StringToVisitConverter;
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
 */
@Component
public class EmrModelConfigurator implements PageModelConfigurator, FragmentModelConfigurator {

	@Autowired
	private KenyaEmrUiUtils kenyaEmrUiUtils;

	@Autowired
	private PatientService patientService;

	@Autowired
	private StringToVisitConverter stringToVisitConverter;

	@Override
	public void configureModel(PageContext pageContext) {
		String patientId = pageContext.getRequest().getRequest().getParameter("patientId");
		String visitId = pageContext.getRequest().getRequest().getParameter("visitId");

		Patient patient = null;
		Visit visit = null;

		if (!StringUtils.isEmpty(patientId) && !StringUtils.isEmpty(visitId)) {
			patient = patientFromParam(patientId);
			visit = stringToVisitConverter.convert(visitId);

			if (!patient.equals(visit.getPatient())) {
				throw new RuntimeException("Mismatch between patient and visit request parameters");
			}
		}
		else if (!StringUtils.isEmpty(patientId)) {
			patient = patientFromParam(patientId);

			List<Visit> activeVisits = Context.getVisitService().getActiveVisitsByPatient(patient);
			visit = activeVisits.size() > 0 ? activeVisits.get(0) : null;
		}
		else if (!StringUtils.isEmpty(visitId)) {
			visit = stringToVisitConverter.convert(visitId);
			patient = visit.getPatient();
		}

		pageContext.getModel().addAttribute("patient", patient);
		pageContext.getModel().addAttribute("visit", visit);
		pageContext.getModel().addAttribute("kenyaEmrUi", kenyaEmrUiUtils);
	}

	@Override
	public void configureModel(FragmentContext fragmentContext) {
		fragmentContext.getModel().addAttribute("kenyaEmrUi", kenyaEmrUiUtils);
	}

	/**
	 * Using this instead of the string to patient converter in UIFR as it isn't accessible during testing
	 * @param id the request parameter value
	 * @return the patient
	 */
	private Patient patientFromParam(String id) {
		if (StringUtils.isEmpty(id)) {
			return null;
		}
		return patientService.getPatient(Integer.valueOf(id));
	}
}