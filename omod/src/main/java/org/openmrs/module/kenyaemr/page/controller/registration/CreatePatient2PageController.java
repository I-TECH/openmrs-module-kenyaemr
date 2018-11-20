/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller.registration;

import org.openmrs.Person;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Create patient step 2 (registration details) page
 */
@AppPage(EmrConstants.APP_REGISTRATION)
public class CreatePatient2PageController {
	
	public void controller(@RequestParam(value = "personId", required = false) Person person,
						   PageModel model) {

		model.addAttribute("person", person);
	}
}