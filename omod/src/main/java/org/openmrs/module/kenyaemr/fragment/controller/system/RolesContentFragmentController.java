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

import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for displaying roles content
 */
public class RolesContentFragmentController {

	public void controller(FragmentModel model) {

		List<SimpleObject> roles = new ArrayList<SimpleObject>();
		List<AppDescriptor> apps = Context.getService(AppFrameworkService.class).getAllApps();

		for (Role role : Context.getUserService().getAllRoles()) {
			List<String> allowedApps = new ArrayList<String>();
			for (AppDescriptor app : apps) {
				if (role.hasPrivilege("App: " + app.getId())) {
					allowedApps.add(app.getLabel());
				}
			}

			roles.add(SimpleObject.create("name", role.getRole(), "allowedApps", allowedApps));
		}

		model.addAttribute("roles", roles);
	}
}