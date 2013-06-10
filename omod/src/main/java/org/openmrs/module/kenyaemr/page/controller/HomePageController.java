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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.AppDescriptor;
import org.openmrs.module.appframework.api.AppFrameworkService;
import org.openmrs.module.kenyaemr.KenyaEmrConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.Redirect;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Home page controller
 */
public class HomePageController {
	
	public String controller(@RequestParam(required=false, value="patientId") Patient patient,
	                         PageModel model, UiUtils ui,
							 HttpServletRequest request) throws Redirect {

		// Redirect to setup page if module is not yet configured
		if (!Context.getService(KenyaEmrService.class).isConfigured()) {
			return "redirect:" + ui.pageLink(KenyaEmrConstants.MODULE_ID, "adminFirstTimeSetup");
		}
		
		AppFrameworkService appService = Context.getService(AppFrameworkService.class);
		List<AppDescriptor> apps = appService.getAppsForUser(Context.getAuthenticatedUser());
		if (apps.size() == 0) {
			// for testing purposes, if we haven't configured this, show all apps
			apps.addAll(appService.getAllApps());
		}
		Collections.sort(apps, new Comparator<AppDescriptor>() {
			
			Integer getScore(AppDescriptor app) {
				if ("kenyaemr.registration".equals(app.getId()))
					return 0;
				else if ("kenyaemr.intake".equals(app.getId()))
					return 1;
				else if ("kenyaemr.medicalEncounter".equals(app.getId()))
					return 2;
				else if ("kenyaemr.medicalChart".equals(app.getId()))
					return 3;
				else if ("kenyaemr.reports".equals(app.getId()))
					return 4;
				else if ("kenyaemr.admin".equals(app.getId()))
					return 5;
				else
					return Integer.MAX_VALUE;
			}
			
			@Override
            public int compare(AppDescriptor left, AppDescriptor right) {
	            return getScore(left).compareTo(getScore(right));
            }
		});

		model.addAttribute("apps", apps);
		model.addAttribute("patient", patient);
		
		return null; // default view
	}
}