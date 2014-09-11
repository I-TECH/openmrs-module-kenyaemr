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

package org.openmrs.module.kenyaemr.fragment.controller.header;

import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.util.ServerInformation;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.Date;
import java.util.Map;

/**
 * Controller for page header
 */
public class PageHeaderFragmentController {

	public void controller(FragmentModel model,
						   @SpringBean KenyaUiUtils kenyaui) {

		Map<String, Object> kenyaemrInfo = ServerInformation.getKenyaemrInformation();

		String moduleVersion = (String) kenyaemrInfo.get("version");
		boolean isSnapshot = moduleVersion.endsWith("SNAPSHOT");

		if (isSnapshot) {
			Date moduleBuildDate = (Date) kenyaemrInfo.get("buildDate");
			moduleVersion += " (" + kenyaui.formatDateTime(moduleBuildDate) + ")";
		}

		model.addAttribute("moduleVersion", moduleVersion);

		model.addAttribute("systemLocation", Context.getService(KenyaEmrService.class).getDefaultLocation());
		model.addAttribute("systemLocationCode", Context.getService(KenyaEmrService.class).getDefaultLocationMflCode());
	}
}