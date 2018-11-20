/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller.developer;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Homepage for the developer app
 */
@AppPage(EmrConstants.APP_DEVELOPER)
public class DeveloperHomePageController {

	public void controller(@RequestParam(value = "section", required = false) String section,
						   PageModel model) {

		if (StringUtils.isEmpty(section)) {
			section = "overview";
		}

		model.addAttribute("section", section);
	}
}