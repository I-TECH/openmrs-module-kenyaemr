/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.header;

import org.openmrs.Patient;
import org.openmrs.module.appframework.domain.AppDescriptor;
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
						   FragmentModel model,
						   PageRequest pageRequest,
						   @SpringBean KenyaUiUtils kenyaUi) {

		model.addAttribute("patient", patient);
		
		AppDescriptor currentApp = kenyaUi.getCurrentApp(pageRequest);

		if (currentApp != null) {
			model.addAttribute("appHomepageUrl", "/" + WebConstants.CONTEXT_PATH + "/" + currentApp.getUrl());
		} else {
			model.addAttribute("appHomepageUrl", null);
		}
	}
}