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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.AppUiUtil;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.module.kenyaemr.KenyaEmrUiUtils;
import org.openmrs.module.kenyaemr.form.FormDescriptor;
import org.openmrs.module.kenyaemr.form.FormDescriptor.Frequency;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.ui.framework.session.Session;

import java.util.*;

/**
 * Fragment to display available forms for a given visit
 */
public class VisitAvailableFormsFragmentController {

	protected static final Log log = LogFactory.getLog(VisitAvailableFormsFragmentController.class);

	public void controller(FragmentModel model,
						   @FragmentParam("visit") Visit visit,
						   UiUtils ui,
						   PageRequest request,
						   @SpringBean KenyaEmr emr,
						   @SpringBean KenyaUiUtils kenyaUi,
						   @SpringBean KenyaEmrUiUtils kenyaEmrUi) {

		String currentApp = kenyaUi.getCurrentApp(request).getId();

		List<FormDescriptor> availableFormDescriptors = emr.getFormManager().getFormsForPatient(currentApp, visit.getPatient(), null);
		List<SimpleObject> availableForms = getAvailableForms(visit, availableFormDescriptors, ui, kenyaEmrUi);

		model.addAttribute("availableForms", availableForms);
	}
	
	/**
     * Gets the list of forms that are actually allowed for the given visit, and converts them to simple objects
	 * @param visit the visit
     * @param forms the list of possible forms for the visit type
     * @return
     */
    private List<SimpleObject> getAvailableForms(Visit visit, List<FormDescriptor> forms, UiUtils ui, KenyaEmrUiUtils kenyaUi) {
    	Set<String> formUuidsThisVisit = new HashSet<String>();
    	for (Encounter e : visit.getEncounters()) {
    		if (!e.getVoided()) {
    			formUuidsThisVisit.add(e.getForm().getUuid());
    		}
    	}
    	
    	List<Encounter> encs = Context.getEncounterService().getEncountersByPatient(visit.getPatient());
    	Set<String> allFormUuids = new HashSet<String>();
    	for (Encounter e : encs) {
    		allFormUuids.add(e.getForm().getUuid());
    	}
    	
    	Map<Program, Date> dateOfActiveEnrollment = new HashMap<Program, Date>();
    	for (PatientProgram pp : Context.getProgramWorkflowService().getPatientPrograms(visit.getPatient(), null, null, null, null, null, false)) {
    		if (pp.getDateCompleted() == null) {
    			dateOfActiveEnrollment.put(pp.getProgram(), pp.getDateEnrolled());
    		}
    	}
    	Map<Program, Set<String>> formUuidsByProgram = new HashMap<Program, Set<String>>();
		for (Map.Entry<Program, Date> e : dateOfActiveEnrollment.entrySet()) {
			Date started = e.getValue();
			Set<String> formUuids = new HashSet<String>();
			for (Encounter enc : encs) {
				if (enc.getEncounterDatetime().compareTo(started) >= 0) {
					formUuids.add(enc.getForm().getUuid());
				}
			}
			formUuidsByProgram.put(e.getKey(), formUuids);
		}
		
    	List<SimpleObject> ret = new ArrayList<SimpleObject>();
		
		for (FormDescriptor config : forms) {
			// Get program for form
			Program formProgram = config.getProgramUuid() != null ? Context.getProgramWorkflowService().getProgramByUuid(config.getProgramUuid()) : null;

			if (formProgram != null && !dateOfActiveEnrollment.keySet().contains(formProgram)) {
				continue;
			}
			boolean allowed = false;
			if (config.getFrequency().equals(Frequency.UNLIMITED)) {
				allowed = true;
			} else if (config.getFrequency().equals(Frequency.VISIT)) {
				allowed = !formUuidsThisVisit.contains(config.getFormUuid());
			} else if (config.getFrequency().equals(Frequency.PROGRAM)) {
				Set<String> formsForProgram = formUuidsByProgram.get(formProgram);
				allowed = formsForProgram == null || !formsForProgram.contains(config.getFormUuid());
			} else if (config.getFrequency().equals(Frequency.ONCE_EVER)) {
				allowed = !allFormUuids.contains(config.getFormUuid());
			} else {
				throw new RuntimeException("Unknown Frequency");
			}
			if (allowed) {
				ret.add(kenyaUi.simpleForm(config, ui));
			}
		}
		
		return ret;
    }
}