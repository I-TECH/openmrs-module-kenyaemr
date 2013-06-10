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
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.kenyaemr.Dictionary;
import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.module.kenyaemr.KenyaEmr;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.form.FormDescriptor;
import org.openmrs.module.kenyaemr.form.FormUtils;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.module.metadatasharing.ImportedPackage;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Homepage for the admin app
 */
@AppPage(EmrWebConstants.APP_ADMIN)
public class AdminHomePageController {

	public void controller(@RequestParam(value = "section", required = false) String section,
						   PageModel model,
						   @SpringBean KenyaEmr emr,
						   @SpringBean ResourceFactory resourceFactory) {

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


			infoCategories.put("Server", general);
			infoCategories.put("Database", content);
		}
		else if (section.equals("modules")) {

			List<SimpleObject> modules = new ArrayList<SimpleObject>();
			for (Module mod : ModuleFactory.getLoadedModules()) {
				modules.add(SimpleObject.create("name", mod.getName(), "version", mod.getVersion(), "status", mod.isStarted()));
			}

			infoCategories.put("Modules", modules);
		}
		else if (section.equals("content")) {

			List<SimpleObject> conceptDictionary = new ArrayList<SimpleObject>();
			conceptDictionary.add(SimpleObject.create("name", "CIEL", "version", Dictionary.getDatabaseVersion(), "status", Dictionary.hasRequiredDatabaseVersion()));

			List<SimpleObject> metadataPackages = new ArrayList<SimpleObject>();
			for (ImportedPackage imported : emr.getMetadataManager().getImportedPackages()) {
				metadataPackages.add(SimpleObject.create("name", imported.getName(), "version", imported.getVersion(), "status", Boolean.TRUE));
			}

			List<SimpleObject> forms = new ArrayList<SimpleObject>();
			for (FormDescriptor descriptor : emr.getFormManager().getAllFormDescriptors()) {
				Form form = Context.getFormService().getFormByUuid(descriptor.getFormUuid());
				Object status = Boolean.TRUE;
				try {
					FormUtils.getHtmlForm(form, resourceFactory);
				} catch (Exception ex) {
					status = "Unable to load XML";
				}
				String name = form.getName() + " (&rarr; " + form.getEncounterType().getName() + ")";
				forms.add(SimpleObject.create("name", name, "version", form.getVersion(), "status", status));
			}

			infoCategories.put("Concepts", conceptDictionary);
			infoCategories.put("Metadata Packages", metadataPackages);
			infoCategories.put("Forms", forms);
		}

		model.addAttribute("section", section);
		model.addAttribute("infoCategories", infoCategories);
	}
}