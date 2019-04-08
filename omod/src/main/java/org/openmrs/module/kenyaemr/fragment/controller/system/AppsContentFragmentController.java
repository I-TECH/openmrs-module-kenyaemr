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