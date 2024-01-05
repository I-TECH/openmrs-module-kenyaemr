/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller.admin;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.kenyacore.identifier.IdentifierManager;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.metadata.CommonMetadata;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * Controller for the guided first-time configuration
 */
@AppPage(EmrConstants.APP_ADMIN)
public class FirstTimeSetupPageController {
	
	public String controller(HttpSession session, PageModel model, UiUtils ui,
							 @SpringBean KenyaUiUtils kenyaUi,
							 @SpringBean IdentifierManager identifierManager,
	                         @RequestParam(required = false, value = "defaultLocation") Location defaultLocation,
	                         @RequestParam(required = false, value = "mrnIdentifierSourceStart") String mrnIdentifierSourceStart,
	                         @RequestParam(required = false, value = "hivIdentifierSourceStart") String hivIdentifierSourceStart) {
		
		KenyaEmrService service = Context.getService(KenyaEmrService.class);
		
		// handle submission
		if (defaultLocation != null || StringUtils.isNotEmpty(mrnIdentifierSourceStart) || StringUtils.isNotEmpty(hivIdentifierSourceStart)) {
			if (defaultLocation != null) {
				service.setDefaultLocation(defaultLocation);
			}
			if (StringUtils.isNotEmpty(mrnIdentifierSourceStart)) {
				service.setupMrnIdentifierSource(mrnIdentifierSourceStart);
			}
			if (StringUtils.isNotEmpty(hivIdentifierSourceStart)) {
				service.setupHivUniqueIdentifierSource(hivIdentifierSourceStart);
			}
			kenyaUi.notifySuccess(session, "First-Time Setup Completed");

			return "redirect:" + ui.pageLink(EmrConstants.MODULE_ID, "home");
		}
		
		defaultLocation = service.getDefaultLocation();
		IdentifierSource mrnIdentifierSource = identifierManager.getIdentifierSource(MetadataUtils.existing(PatientIdentifierType.class, CommonMetadata._PatientIdentifierType.OPENMRS_ID));
		IdentifierSource hivIdentifierSource = identifierManager.getIdentifierSource(MetadataUtils.existing(PatientIdentifierType.class, HivMetadata._PatientIdentifierType.UNIQUE_PATIENT_NUMBER));

		User authenticatedUser = Context.getAuthenticatedUser();
		
		model.addAttribute("isSuperUser", authenticatedUser != null ? Context.getAuthenticatedUser().isSuperUser() : false);
		model.addAttribute("defaultLocation", defaultLocation);
		model.addAttribute("mrnIdentifierSource", mrnIdentifierSource);
		model.addAttribute("hivIdentifierSource", hivIdentifierSource);
		return null;
	}
}