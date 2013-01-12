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
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.AppUiUtil;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.form.FormConfig;
import org.openmrs.module.kenyaemr.form.FormManager;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.session.Session;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Viewing a patient's record, in the medicalChart app
 */
public class MedicalChartViewPatientPageController {

	public void controller(@RequestParam("patientId") Patient patient,
	                       @RequestParam(required = false, value = "visitId") Visit visit,
	                       @RequestParam(required = false, value = "formUuid") String formUuid,
	                       @RequestParam(required = false, value = "patientProgramId") PatientProgram pp,
	                       PageModel model,
	                       UiUtils ui,
	                       Session session) {

		if ("".equals(formUuid)) {
			formUuid = null;
		}

		AppUiUtil.startApp("kenyaemr.medicalChart", session);
		
		recentlyViewed(patient, session);
		
		model.addAttribute("patient", patient);
		model.addAttribute("person", patient);

		List<FormConfig> oneTimeFormConfigs = FormManager.getFormsForPatient("kenyaemr.medicalChart", patient, Collections.singleton(FormConfig.Frequency.ONCE_EVER));
		List<SimpleObject> oneTimeForms = new ArrayList<SimpleObject>();
		for (FormConfig formConfig : oneTimeFormConfigs) {
			Form form = Context.getFormService().getFormByUuid(formConfig.getFormUuid());
			//HtmlForm hf = Context.getService(HtmlFormEntryService.class).getHtmlFormByForm(form);
			oneTimeForms.add(SimpleObject.create("formUuid", form.getUuid(), "label", form.getName(), "iconProvider", formConfig.getIconProvider(), "icon", formConfig.getIcon()));
		}
		model.addAttribute("oneTimeForms", oneTimeForms);
		
		List<FormConfig> retrospectiveFormConfigs = FormManager.getFormsForPatientByEncounterType("kenyaemr.medicalChart", patient, Collections.singleton(FormConfig.Frequency.VISIT), MetadataConstants.RETROSPECTIVE_ENCOUNTER_TYPE_UUID);
		List<SimpleObject> retrospectiveForms = new ArrayList<SimpleObject>();
		for (FormConfig formConfig : retrospectiveFormConfigs) {
			Form form = Context.getFormService().getFormByUuid(formConfig.getFormUuid());
			//HtmlForm hf = Context.getService(HtmlFormEntryService.class).getHtmlFormByForm(form);
			retrospectiveForms.add(SimpleObject.create("formUuid", form.getUuid(), "label", form.getName(), "iconProvider", formConfig.getIconProvider(), "icon", formConfig.getIcon()));
		}
		model.addAttribute("retrospectiveForms", retrospectiveForms);

		model.addAttribute("programs", Context.getProgramWorkflowService().getPatientPrograms(patient, null, null, null, null, null, false));
		model.addAttribute("program", pp);
		
		model.addAttribute("visits", Context.getVisitService().getVisitsByPatient(patient));
		model.addAttribute("visit", visit);
		
		Form form = null;
		Encounter encounter = null;
		String retrospective = null;
		
		String selection = "overview";
		
		if (visit != null) {
			selection = "visit-" + visit.getVisitId();
		}
		else if (formUuid != null) {
			selection = "form-" + formUuid;
			
			form = Context.getFormService().getFormByUuid(formUuid);
			if (!form.getEncounterType().getUuid().equals(MetadataConstants.RETROSPECTIVE_ENCOUNTER_TYPE_UUID)) {
				List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, null, null, Collections.singleton(form), null, null, null, null, false);
				encounter = encounters.size() > 0 ? encounters.get(0) : null;
			} else {
				Visit newVisit = new Visit();
				VisitService vs = Context.getVisitService();
				
				newVisit.setPatient(patient);
				newVisit.setStartDatetime(new Date());
				newVisit.setVisitType(vs.getVisitTypeByUuid(MetadataConstants.OUTPATIENT_VISIT_TYPE_UUID));
				model.addAttribute("newREVisit", newVisit);
				retrospective = "true";
			}
		}
		else if (pp != null) {
			selection = "program-" + pp.getPatientProgramId();
		}

		model.addAttribute("form", form);
		model.addAttribute("encounter", encounter);
		model.addAttribute("selection", selection);
		model.addAttribute("retrospective", retrospective);
	}

	/**
     * Remember that this patient was just viewed
     */
    private void recentlyViewed(Patient patient, Session session) {
	    LinkedList<Integer> recent = session.getAttribute("kenyaemr.medicalChart.recentlyViewedPatients", LinkedList.class);
	    if (recent == null) {
	    	recent = new LinkedList<Integer>();
	    	session.setAttribute("kenyaemr.medicalChart.recentlyViewedPatients", recent);
	    }
	    recent.removeFirstOccurrence(patient.getPatientId());
	    recent.add(0, patient.getPatientId());
	    while (recent.size() > 10)
	    	recent.removeLast();
    }
	
}
