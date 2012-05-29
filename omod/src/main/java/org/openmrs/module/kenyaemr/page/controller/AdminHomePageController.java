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

import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.appframework.AppUiUtil;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.session.Session;
import org.openmrs.util.OpenmrsConstants;


/**
 * Homepage for the "Admin" app
 */
public class AdminHomePageController {

	public void controller(Session session, PageModel model) {
		AppUiUtil.startApp("kenyaemr.admin", session);
		
		SimpleObject overall = SimpleObject.create("OpenMRS version", OpenmrsConstants.OPENMRS_VERSION,
			"Total Patients", Context.getPatientSetService().getPatientsByCharacteristics(null, null, null).size());
		
		Map<String, Object> moduleVersions = new LinkedHashMap<String, Object>();
		for (Module mod : ModuleFactory.getStartedModules()) {
			moduleVersions.put(mod.getModuleId(), mod.getVersion());
		}
		
		Map<String, Object> metadataVersions = new LinkedHashMap<String, Object>();
		for (ImportedPackage imported : Context.getService(MetadataSharingService.class).getAllImportedPackages()) {
			metadataVersions.put(imported.getName(), imported.getVersion());
		}

		Map<String, Object> info = new LinkedHashMap<String, Object>();
		info.put("General Information", overall);
		info.put("Module Versions", moduleVersions);
		info.put("Metadata Versions", metadataVersions);
		info.put("Recent Errors", "Ideally we can record errors that occur (especially if the end-user sees them) and allow them to be reported from here. (In a first pass this would just mean giving the admin a textarea to be copied to the clipboard.)");

		model.addAttribute("info", info);
	}
	
}
