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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.AppDescriptor;
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
	
	public void controller(FragmentModel model,
	                       @FragmentParam("visit") Visit visit,
	                       Session session) {
		// available forms are defined by the running app
		AppDescriptor app = AppUiUtil.getCurrentApp(session).getApp();

		List<String> formUuids;
		if (app.getId().equals("kenyaemr.registration")) {
			formUuids = Arrays.asList(MetadataConstants.VITALS_AND_TRIAGE_HTML_FORM_UUID);
		} else if (app.getId().equals("kenyaemr.intake")) {
			formUuids = Arrays.asList(MetadataConstants.VITALS_AND_TRIAGE_HTML_FORM_UUID, MetadataConstants.PAST_MEDICAL_HISTORY_AND_SURGICAL_HISTORY_HTML_FORM_UUID, MetadataConstants.ART_HISTORY_HTML_FORM_UUID, MetadataConstants.CLINICAL_ENCOUNTER_HTML_FORM_UUID, MetadataConstants.TB_SCREENING_HTML_FORM_UUID, MetadataConstants.FAMILY_PLANNING_AND_PREGNANCY_HTML_FORM_UUID, MetadataConstants.LAB_RESULTS_HTML_FORM_UUID);
		} else if (app.getId().equals("kenyaemr.medicalEncounter")) {
			formUuids = Arrays.asList(MetadataConstants.PAST_MEDICAL_HISTORY_AND_SURGICAL_HISTORY_HTML_FORM_UUID, MetadataConstants.ART_HISTORY_HTML_FORM_UUID, MetadataConstants.CLINICAL_ENCOUNTER_HTML_FORM_UUID, MetadataConstants.TB_SCREENING_HTML_FORM_UUID, MetadataConstants.FAMILY_PLANNING_AND_PREGNANCY_HTML_FORM_UUID, MetadataConstants.IMPRESSIONS_AND_DIAGNOSES_HTML_FORM_UUID, MetadataConstants.LAB_RESULTS_HTML_FORM_UUID);
		} else {
			throw new RuntimeException("No suitable running app");
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
		
		List<SimpleObject> availableForms = new ArrayList<SimpleObject>();
		Set<Encounter> editableEncounters = new HashSet<Encounter>();
		
		Map<String, HtmlForm> formByUuid = new HashMap<String, HtmlForm>();
		for (HtmlForm hf : Context.getService(HtmlFormEntryService.class).getAllHtmlForms()) {
			formByUuid.put(hf.getUuid(), hf);
		}
		
		for (String uuid : formUuids) {
			HtmlForm hf = formByUuid.get(uuid);
			boolean found = false;
			for (Encounter e : encounters) {
				if (hf.getForm().equals(e.getForm())) {
					found = true;
					editableEncounters.add(e);
				}
			}
			if (!found) {
				availableForms.add(SimpleObject.create("htmlFormId", hf.getId(), "label", hf.getName(), "icon", "activity_monitor_add.png"));
			}
		}
		
		model.addAttribute("encounters", encounters);
		model.addAttribute("availableForms", availableForms);
		model.addAttribute("editableEncounters", editableEncounters);
	}
	
}
