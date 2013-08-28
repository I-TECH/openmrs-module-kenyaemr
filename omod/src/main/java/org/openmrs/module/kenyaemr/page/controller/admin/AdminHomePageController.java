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

package org.openmrs.module.kenyaemr.page.controller.admin;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Homepage for the admin app
 */
@AppPage(EmrConstants.APP_ADMIN)
public class AdminHomePageController {

	public void controller(@RequestParam(value = "section", required = false) String section,
						   PageModel model) {

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

		model.addAttribute("section", section);
		model.addAttribute("infoCategories", infoCategories);
	}
}