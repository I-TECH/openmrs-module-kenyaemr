/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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