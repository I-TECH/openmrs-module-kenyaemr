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

import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.appframework.AppUiUtil;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.session.Session;
import org.openmrs.util.OpenmrsConstants;


/**
 * Homepage for the "Admin" app
 */
public class AdminHomePageController {

	public void controller(Session session, PageModel model) {
		AppUiUtil.startApp("kenyaemr.admin", session);
		
		Map<String, Object> info = new LinkedHashMap<String, Object>();
		info.put("OpenMRS version", OpenmrsConstants.OPENMRS_VERSION);
		for (Module mod : ModuleFactory.getStartedModules()) {
			info.put(mod.getModuleId() + " version", mod.getVersion());
		}

		model.addAttribute("info", info);
	}
	
}
