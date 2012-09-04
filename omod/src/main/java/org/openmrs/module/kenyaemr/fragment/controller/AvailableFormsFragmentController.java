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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openmrs.Encounter;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.AppUiUtil;
import org.openmrs.module.htmlformentry.HtmlForm;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.session.Session;

/**
 *
 */
public class AvailableFormsFragmentController {
	
	public void controller(FragmentModel model, @FragmentParam("visit") Visit visit, Session session) {
		// TODO get by uuid
		Program hivProgram = Context.getProgramWorkflowService().getProgramByUuid(MetadataConstants.HIV_PROGRAM_UUID);
		
		List<AvailableFormConfig> forms = new ArrayList<AvailableFormConfig>();
		String currentApp = AppUiUtil.getCurrentApp(session).getApp().getId();
		
		if ("kenyaemr.registration".equals(currentApp)) {
			forms.add(new AvailableFormConfig(MetadataConstants.TRIAGE_FORM_UUID, Frequency.VISIT, null));
		} else if ("kenyaemr.intake".equals(currentApp)) {
			forms.add(new AvailableFormConfig(MetadataConstants.TRIAGE_FORM_UUID, Frequency.VISIT, null));
			forms.add(new AvailableFormConfig(MetadataConstants.TB_SCREENING_FORM_UUID, Frequency.VISIT, null));
			forms.add(new AvailableFormConfig(MetadataConstants.PROGRESS_NOTE_FORM_UUID, Frequency.VISIT, null));
			forms.add(new AvailableFormConfig(MetadataConstants.MOH_257_ENCOUNTER_ORDER_LAB_INVESTIGATIONS_FORM_UUID, Frequency.VISIT, null));
		} else {
			//forms.add(new AvailableFormConfig(MetadataConstants.TRIAGE_FORM_UUID, Frequency.VISIT, null));
			forms.add(new AvailableFormConfig(MetadataConstants.CLINICAL_ENCOUNTER_FORM_UUID, Frequency.VISIT, null));
			forms.add(new AvailableFormConfig(MetadataConstants.CLINICAL_ENCOUNTER_HIV_ADDENDUM_FORM_UUID, Frequency.VISIT, hivProgram));
			forms.add(new AvailableFormConfig(MetadataConstants.TB_SCREENING_FORM_UUID, Frequency.VISIT, null));
			forms.add(new AvailableFormConfig(MetadataConstants.PROGRESS_NOTE_FORM_UUID, Frequency.VISIT, null));
			forms.add(new AvailableFormConfig(MetadataConstants.MOH_257_ENCOUNTER_ORDER_LAB_INVESTIGATIONS_FORM_UUID, Frequency.VISIT, null));
			forms.add(new AvailableFormConfig(MetadataConstants.OTHER_MEDICATIONS_FORM_UUID, Frequency.VISIT, null));
		}
		
		List<Encounter> encounters = new ArrayList<Encounter>(visit.getEncounters());
		CollectionUtils.filter(encounters, new Predicate() {
			@Override
			public boolean evaluate(Object enc) {
				return !((Encounter) enc).getVoided();
			}
		});
		Collections.sort(encounters, new Comparator<Encounter>() {
			@Override
			public int compare(Encounter left, Encounter right) {
				return left.getEncounterDatetime().compareTo(right.getEncounterDatetime());
			}
		});
		
		List<SimpleObject> availableForms = getAvailableForms(visit, forms);
		
		model.addAttribute("encounters", encounters);
		model.addAttribute("availableForms", availableForms);
	}
	
	/**
     * @param visit
     * @param forms
     * @return
     */
    private List<SimpleObject> getAvailableForms(Visit visit, List<AvailableFormConfig> forms) {
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
    	
		Map<String, HtmlForm> formByUuid = new HashMap<String, HtmlForm>();
		for (HtmlForm hf : Context.getService(HtmlFormEntryService.class).getAllHtmlForms()) {
			formByUuid.put(hf.getForm().getUuid(), hf);
		}
		
    	List<SimpleObject> ret = new ArrayList<SimpleObject>();
		
		for (AvailableFormConfig config : forms) {
			if (config.getForProgram() != null && !dateOfActiveEnrollment.keySet().contains(config.getForProgram())) {
				continue;
			}
			boolean allowed = false;
			if (config.getFrequency().equals(Frequency.UNLIMITED)) {
				allowed = true;
			} else if (config.getFrequency().equals(Frequency.VISIT)) {
				allowed = !formUuidsThisVisit.contains(config.getFormUuid());
			} else if (config.getFrequency().equals(Frequency.PROGRAM)) {
				Set<String> formsForProgram = formUuidsByProgram.get(config.getForProgram());
				allowed = formsForProgram == null || !formsForProgram.contains(config.getFormUuid());
			} else if (config.getFrequency().equals(Frequency.ONCE_EVER)) {
				allowed = !allFormUuids.contains(config.getFormUuid());
			} else {
				throw new RuntimeException("Unknown Frequency");
			}
			if (allowed) {
				HtmlForm hf = formByUuid.get(config.getFormUuid());
				if (hf == null) {
					throw new RuntimeException("No htmlform with uuid " + config.getFormUuid());
				}
				ret.add(SimpleObject.create("htmlFormId", hf.getId(), "label", hf.getName(), "iconProvider", config.getIconProvider(), "icon", config.getIcon()));
			}
		}
		
		return ret;
    }

	public enum Frequency {
		ONCE_EVER, PROGRAM, VISIT, UNLIMITED
	}
	
	public class AvailableFormConfig {
		
		private String formUuid;
		
		private Program forProgram;
		
		private Frequency frequency = Frequency.VISIT;
		
		private String iconProvider = "uilibrary";
		
		private String icon = "page_blank_add_32.png";
		
		/**
		 * @param formUuid
		 * @param frequency
		 * @param forProgram
		 */
		public AvailableFormConfig(String formUuid, Frequency frequency, Program forProgram) {
			this.formUuid = formUuid;
			this.frequency = frequency;
			this.forProgram = forProgram;
		}
		
		/**
		 * @return the formUuid
		 */
		public String getFormUuid() {
			return formUuid;
		}
		
		/**
		 * @param formUuid the formUuid to set
		 */
		public void setFormUuid(String formUuid) {
			this.formUuid = formUuid;
		}
		
		/**
		 * @return the forProgram
		 */
		public Program getForProgram() {
			return forProgram;
		}
		
		/**
		 * @param forProgram the forProgram to set
		 */
		public void setForProgram(Program forProgram) {
			this.forProgram = forProgram;
		}
		
		/**
		 * @return the frequency
		 */
		public Frequency getFrequency() {
			return frequency;
		}
		
		/**
		 * @param frequency the frequency to set
		 */
		public void setFrequency(Frequency frequency) {
			this.frequency = frequency;
		}
		
		/**
		 * @return the iconProvider
		 */
		public String getIconProvider() {
			return iconProvider;
		}
		
		/**
		 * @param iconProvider the iconProvider to set
		 */
		public void setIconProvider(String iconProvider) {
			this.iconProvider = iconProvider;
		}
		
		/**
		 * @return the icon
		 */
		public String getIcon() {
			return icon;
		}
		
		/**
		 * @param icon the icon to set
		 */
		public void setIcon(String icon) {
			this.icon = icon;
		}
		
	}
	
}
