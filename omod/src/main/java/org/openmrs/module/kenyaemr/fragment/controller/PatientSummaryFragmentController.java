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

import org.openmrs.Patient;
import org.openmrs.module.appframework.AppDescriptor;
import org.openmrs.module.kenyacore.CoreContext;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Patient summary fragment
 */
public class PatientSummaryFragmentController {
	
	public void controller(@FragmentParam("patient") Patient patient,
						   @SpringBean CoreContext emr,
						   @SpringBean KenyaUiUtils kenyaUi,
						   PageRequest pageRequest,
						   UiUtils ui,
						   FragmentModel model) {

		AppDescriptor currentApp = kenyaUi.getCurrentApp(pageRequest);

		List<SimpleObject> forms = new ArrayList<SimpleObject>();

		for (FormDescriptor formDescriptor : emr.getFormManager().getFormsForPatient(currentApp, patient)) {
			forms.add(ui.simplifyObject(formDescriptor.getTarget()));
		}

		model.addAttribute("patient", patient);
		model.addAttribute("forms", forms);
	}
}