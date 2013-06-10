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

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Visit;
import org.openmrs.module.appframework.AppUiUtil;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.module.kenyaemr.form.FormDescriptor;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.ui.framework.session.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Fragment to display completed forms for a given visit
 */
public class VisitCompletedFormsFragmentController {
	
	public void controller(FragmentModel model,
						   @FragmentParam("visit") Visit visit,
						   PageRequest request,
						   @SpringBean KenyaEmr emr,
						   @SpringBean KenyaUiUtils kenyaUi) {

		List<Encounter> allEncounters = new ArrayList<Encounter>(visit.getEncounters());

		final String currentApp = kenyaUi.getCurrentApp(request).getId();
		List<FormDescriptor> descriptorsForApp = emr.getFormManager().getFormDescriptorsForApp(currentApp);

		List<Encounter> encounters = new ArrayList<Encounter>();
		for (Encounter encounter : allEncounters) {
			if (encounter.isVoided()) {
				continue;
			}

			Form form = encounter.getForm();
			if (form != null) {
				FormDescriptor descriptor = emr.getFormManager().getFormDescriptor(form);
				if (!descriptorsForApp.contains(descriptor)) {
					continue;
				}
			}

			encounters.add(encounter);
		}

		Collections.sort(encounters, new Comparator<Encounter>() {
			@Override
			public int compare(Encounter left, Encounter right) {
				return left.getEncounterDatetime().compareTo(right.getEncounterDatetime());
			}
		});

		model.addAttribute("encounters", encounters);
	}
}