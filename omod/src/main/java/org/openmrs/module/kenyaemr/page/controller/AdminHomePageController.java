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

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.appframework.AppUiUtil;
import org.openmrs.module.kenyaemr.KenyaEmrConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.regimen.RegimenManager;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.session.Session;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Homepage for the "Admin" app
 */
public class AdminHomePageController {

	public void controller(@RequestParam(value = "section", required = false) String section, Session session, UiUtils ui, PageModel model) {
		AppUiUtil.startApp("kenyaemr.admin", session);

		if (StringUtils.isEmpty(section)) {
			section = "overview";
		}

		Map<String, Object> infoCategories = new LinkedHashMap<String, Object>();

		if (section.equals("overview")) {

			KenyaEmrService svc = Context.getService(KenyaEmrService.class);
			String facility = svc.getDefaultLocation() + " (" + svc.getDefaultLocationMflCode() + ")";

			List<SimpleObject> general = new ArrayList<SimpleObject>();
			general.add(SimpleObject.create("label", "OpenMRS version", "value", OpenmrsConstants.OPENMRS_VERSION));
			general.add(SimpleObject.create("label", "Facility", "value", facility));
			general.add(SimpleObject.create("label", "Server timezone", "value", Calendar.getInstance().getTimeZone().getDisplayName()));

			List<SimpleObject> content = new ArrayList<SimpleObject>();
			content.add(SimpleObject.create("label", "Total patients", "value", Context.getPatientSetService().getPatientsByCharacteristics(null, null, null).size()));
			content.add(SimpleObject.create("label", "Total providers", "value", Context.getProviderService().getAllProviders().size()));
			content.add(SimpleObject.create("label", "Total users", "value", Context.getUserService().getAllUsers().size()));
			content.add(SimpleObject.create("label", "Total visits", "value", Context.getVisitService().getAllVisits().size()));

			List<SimpleObject> metadataPackages = new ArrayList<SimpleObject>();
			for (ImportedPackage imported : Context.getService(MetadataSharingService.class).getAllImportedPackages()) {
				metadataPackages.add(SimpleObject.fromObject(imported, ui, "name", "version", "imported"));
			}

			// Regimens aren't actually imported from a metadata package but let's pretend for the sake of simplicity
			metadataPackages.add(SimpleObject.create("name", "Kenya EMR Regimens", "version", RegimenManager.getDefinitionsVersion(), "imported", true));

			// Nor are concepts...
			String conceptsVersion = Context.getAdministrationService().getGlobalProperty(KenyaEmrConstants.GP_CONCEPTS_VERSION, null);
			metadataPackages.add(SimpleObject.create("name", "Kenya EMR Concepts", "version", conceptsVersion, "imported", (conceptsVersion != null)));

			infoCategories.put("Server", general);
			infoCategories.put("Database", content);
			infoCategories.put("Metadata Packages", metadataPackages);
		}
		else if (section.equals("modules")) {

			List<SimpleObject> modules = new ArrayList<SimpleObject>();
			for (Module mod : ModuleFactory.getLoadedModules()) {
				modules.add(SimpleObject.fromObject(mod, ui, "name", "version", "started"));
			}

			infoCategories.put("Modules", modules);
		}

		model.addAttribute("section", section);
		model.addAttribute("infoCategories", infoCategories);
	}
}