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

package org.openmrs.module.kenyaemr.fragment.controller.header;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.kenyacore.identifier.IdentifierManager;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.ui.framework.WebConstants;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageRequest;

/**
 * Banner showing which patient this page is in the context of
 */
public class PatientHeaderFragmentController {

	public void controller(@FragmentParam("patient") Patient patient,
						   @FragmentParam(value = "visit", required = false) Visit visit,
						   FragmentModel model,
						   PageRequest pageRequest,
						   @SpringBean KenyaUiUtils kenyaUi,
						   @SpringBean IdentifierManager identifierManager) {

		model.addAttribute("patient", patient);
		model.addAttribute("visit", visit);
		model.addAttribute("visitStartedToday", visit != null && EmrUtils.isToday(visit.getStartDatetime()));
		
		AppDescriptor currentApp = kenyaUi.getCurrentApp(pageRequest);

		if (currentApp != null) {
			model.addAttribute("appHomepageUrl", "/" + WebConstants.CONTEXT_PATH + "/" + currentApp.getUrl());
		} else {
			model.addAttribute("appHomepageUrl", null);
		}

		model.addAttribute("idsToShow", identifierManager.getPatientDisplayIdentifiers(patient));
	}
}