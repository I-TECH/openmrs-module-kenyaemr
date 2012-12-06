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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.appframework.AppUiUtil;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.session.Session;
import org.openmrs.util.OpenmrsConstants;

/**
 * Homepage for the "Admin" app
 */
public class AdminHomePageController {

	public void controller(Session session, UiUtils ui, PageModel model) {
		AppUiUtil.startApp("kenyaemr.admin", session);

		List<SimpleObject> general = new ArrayList<SimpleObject>();
		general.add(SimpleObject.create("label", "OpenMRS version", "value", OpenmrsConstants.OPENMRS_VERSION));
		general.add(SimpleObject.create("label", "Total Patients", "value", Context.getPatientSetService().getPatientsByCharacteristics(null, null, null).size()));
		general.add(SimpleObject.create("label", "Total Providers", "value", Context.getProviderService().getAllProviders().size()));
		general.add(SimpleObject.create("label", "Total Users", "value", Context.getUserService().getAllUsers().size()));
		
		List<SimpleObject> modules = new ArrayList<SimpleObject>();
		for (Module mod : ModuleFactory.getLoadedModules()) {
			modules.add(SimpleObject.fromObject(mod, ui, "name", "version", "started"));
		}

		List<SimpleObject> metadataPackages = new ArrayList<SimpleObject>();
		for (ImportedPackage imported : Context.getService(MetadataSharingService.class).getAllImportedPackages()) {
			metadataPackages.add(SimpleObject.fromObject(imported, ui, "name", "version", "imported"));
		}

		Map<String, Object> info = new LinkedHashMap<String, Object>();
		info.put("General Information", general);
		info.put("Modules", modules);
		info.put("Metadata Packages", metadataPackages);

		// TODO implement this.
		// Ideally we can record errors that occur (especially if the end-user sees them) and allow them to be reported from here.
		// In a first pass this would just mean giving the admin a textarea to be copied to the clipboard.
		//info.put("Recent Errors", ...);

		model.addAttribute("info", info);
	}
}