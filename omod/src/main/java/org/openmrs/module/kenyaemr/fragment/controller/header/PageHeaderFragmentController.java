/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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