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

package org.openmrs.module.kenyaemr.fragment.controller.system;

import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for displaying all app content
 */
public class AppsContentFragmentController {

	public void controller(FragmentModel model) {
		List<SimpleObject> apps = new ArrayList<SimpleObject>();
		for (AppDescriptor app : Context.getService(AppFrameworkService.class).getAllApps()) {

			apps.add(SimpleObject.create("id", app.getId(), "label", app.getLabel(), "url", app.getUrl()));
		}

		model.addAttribute("apps", apps);
	}
}