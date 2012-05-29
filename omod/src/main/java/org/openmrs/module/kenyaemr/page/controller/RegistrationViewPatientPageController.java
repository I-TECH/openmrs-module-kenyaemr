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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.AppUiUtil;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.session.Session;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 */
public class RegistrationViewPatientPageController {
	
	public void controller(@RequestParam("patientId") Patient patient,
	                       @RequestParam(value="visitId", required=false) Visit visit,
	                       PageModel model,
	                       Session session) {
		
		AppUiUtil.startApp("kenyaemr.registration", session);

		model.addAttribute("patient", patient);
		model.addAttribute("person", patient);

		List<Visit> currentVisits = new ArrayList<Visit>();
		List<Visit> pastVisits = new ArrayList<Visit>();
		
		for (Visit v : Context.getVisitService().getVisitsByPatient(patient)) {
			// TODO only include visits at this location and its sub-locations
			if (v.getStopDatetime() == null) {
				currentVisits.add(v);
			} else {
				pastVisits.add(v);
			}
		}
		model.addAttribute("pastVisits", pastVisits);
		model.addAttribute("currentVisits", currentVisits);
		
		if (visit == null && currentVisits.size() > 0) {
			visit = currentVisits.get(0);
		}
		
		List<SimpleObject> availableForms = new ArrayList<SimpleObject>();
		if (visit != null) {
			Set<String> encounterTypesAlready = new HashSet<String>();
			for (Encounter e : visit.getEncounters()) {
				if (e.isVoided())
					continue;
				encounterTypesAlready.add(e.getEncounterType().getUuid());
			}
			
			for (HtmlForm hf : Context.getService(HtmlFormEntryService.class).getAllHtmlForms()) {
				if (!encounterTypesAlready.contains(hf.getForm().getEncounterType().getUuid())) {
					availableForms.add(SimpleObject.create("htmlFormId", hf.getId(), "label", hf.getName(), "icon", "activity_monitor_add.png"));
				}
			}
			
			/*
			if (!encounterTypesAlready.contains(MetadataConstants.VITALS_ENCOUNTER_TYPE_UUID)) {
				// TODO this needs to be defined by UUID
				availableForms.add(SimpleObject.create("htmlFormId", 2, "label", "Vitals", "icon", "activity_monitor_add.png"));
			}
			*/
		}
			
		model.addAttribute("visit", visit);
		model.addAttribute("availableForms", availableForms);
		
		if (currentVisits.size() == 0) {
			Visit newVisit = new Visit();
			newVisit.setPatient(patient);
			newVisit.setStartDatetime(new Date());
			model.addAttribute("newCurrentVisit", newVisit);
		}
		
		{
			Visit pastVisit = new Visit();
			pastVisit.setPatient(patient);
			model.addAttribute("newPastVisit", pastVisit);
		}
	}
	
}
