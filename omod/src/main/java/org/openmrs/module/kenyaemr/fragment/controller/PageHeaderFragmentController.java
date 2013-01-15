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
package org.openmrs.module.kenyaemr.fragment.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.kenyaemr.KenyaEmrConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.util.ContextProvider;
import org.openmrs.module.kenyaemr.util.KenyaEmrUtils;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class PageHeaderFragmentController {

	private static final DateFormat buildDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
	
	public void controller(FragmentModel model) throws ParseException {
		// Get module version
		String moduleVersion = KenyaEmrUtils.getModuleVersion();

		// Fetch build date for snapshot versions
		Date moduleBuildDate = null;
		if (moduleVersion.endsWith("SNAPSHOT")) {
			Map<String, String> buildProperties = KenyaEmrUtils.getModuleBuildProperties();
			moduleBuildDate = buildDateFormat.parse(buildProperties.get("buildDate"));
		}

		model.addAttribute("moduleVersion", moduleVersion);
		model.addAttribute("moduleBuildDate", moduleBuildDate);

		try {
			model.addAttribute("systemLocation", Context.getService(KenyaEmrService.class).getDefaultLocation());
		} catch (Exception ex) {
			// don't complain about privilege exceptions if not logged in
			model.addAttribute("systemLocation", null);
		}
	}
}
