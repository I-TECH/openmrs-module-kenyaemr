/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller;

import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Page for entering a new form
 */
@SharedPage
public class EnterFormPageController {

	public void controller(@RequestParam(value = "formUuid", required = false) String formUuid,
	                       @RequestParam("returnUrl") String returnUrl,
	                       PageModel model) {

		model.addAttribute("formUuid", formUuid);
		model.addAttribute("returnUrl", returnUrl);
	}
}