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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Visit;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.kenyacore.form.FormDescriptor;
import org.openmrs.module.kenyacore.form.FormManager;
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

		for (FormDescriptor descriptor : formManager.getAllUncompletedFormsForVisit(currentApp, visit)) {
			//Display only active forms
			if(!descriptor.getTarget().isRetired()) {
				availableForms.add(ui.simplifyObject(descriptor.getTarget()));
			}
			continue;
		}

		model.addAttribute("availableForms", availableForms);
	}
}