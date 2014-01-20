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

import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.OpenmrsUtil;

import javax.servlet.http.HttpSession;

/**
 * Home page controller
 */
public class HomePageController {
	
	public String controller(PageModel model, UiUtils ui, HttpSession session, @SpringBean KenyaUiUtils kenyaUi) {

		// Redirect to setup page if module is not yet configured
		if (Context.getService(KenyaEmrService.class).isSetupRequired()) {
			kenyaUi.notifySuccess(session, "First-Time Setup Needed");
			return "redirect:" + ui.pageLink(EmrConstants.MODULE_ID, "admin/firstTimeSetup");
		}

		// Get apps for the current user
		List<AppDescriptor> apps = Context.getService(AppFrameworkService.class).getAppsForCurrentUser();

		// Sort by order property
		Collections.sort(apps, new Comparator<AppDescriptor>() {
			@Override
			public int compare(AppDescriptor left, AppDescriptor right) {
				return OpenmrsUtil.compareWithNullAsGreatest(left.getOrder(), right.getOrder());
			}
		});

		model.addAttribute("apps", apps);
		
		return null;
	}
}