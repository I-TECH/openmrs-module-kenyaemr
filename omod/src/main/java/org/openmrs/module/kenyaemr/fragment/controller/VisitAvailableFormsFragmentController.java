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
import org.openmrs.Visit;
import org.openmrs.module.appframework.AppDescriptor;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyacore.form.FormManager;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageRequest;

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
						   @SpringBean FormManager formManager,
						   @SpringBean KenyaUiUtils kenyaUi) {

		AppDescriptor currentApp = kenyaUi.getCurrentApp(request);

		List<SimpleObject> availableForms = new ArrayList<SimpleObject>();

		for (FormDescriptor descriptor : formManager.getUncompletedFormsForVisit(currentApp, visit)) {
			availableForms.add(ui.simplifyObject(descriptor.getTarget()));
		}

		model.addAttribute("availableForms", availableForms);
	}
}