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
package org.openmrs.module.kenyaemr.page.controller;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.api.impl.ConfigurationRequiredException;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.WebConstants;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.session.Session;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for the guided first-time configuration
 */
public class AdminFirstTimeSetupPageController {
	
	public String controller(Session session, PageModel model, UiUtils ui,
	                         @RequestParam(required = false, value = "defaultLocation") Location defaultLocation) {
		
		KenyaEmrService service = Context.getService(KenyaEmrService.class);
		
		if (defaultLocation != null) {
			service.setDefaultLocation(defaultLocation);
			session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "First-Time Setup Completed");
			return "redirect:" + ui.pageLink("kenyaHome");
		}
		
		if (!service.isConfigured()) {
			session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "First-Time Setup Needed");
		}
		
		try {
			defaultLocation = service.getDefaultLocation();
		} catch (ConfigurationRequiredException ex) {
			// pass
		}
		
		model.addAttribute("isSuperUser", Context.getAuthenticatedUser().isSuperUser());
		model.addAttribute("defaultLocation", defaultLocation);
		return null;
	}
	
}
