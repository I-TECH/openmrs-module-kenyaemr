/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller;

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Visit;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyacore.form.FormManager;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Fragment to display completed forms for a given visit
 */
public class VisitCompletedFormsFragmentController {

	public void controller(@FragmentParam("visit") Visit visit,
						   FragmentModel model,
						   PageRequest request,
						   @SpringBean FormManager formManager,
						   @SpringBean KenyaUiUtils kenyaUi) {

		List<Encounter> allEncounters = new ArrayList<Encounter>(visit.getEncounters());

		final AppDescriptor currentApp = kenyaUi.getCurrentApp(request);

		List<FormDescriptor> completedForms = formManager.getCompletedFormsForVisit(currentApp, visit);

		List<Encounter> encounters = new ArrayList<Encounter>();
		for (Encounter encounter : allEncounters) {
			Form form = encounter.getForm();

			if (encounter.isVoided() || form == null) {
				continue;
			}

			FormDescriptor descriptor = formManager.getFormDescriptor(form);

			if (completedForms.contains(descriptor)) {
				encounters.add(encounter);
			}
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