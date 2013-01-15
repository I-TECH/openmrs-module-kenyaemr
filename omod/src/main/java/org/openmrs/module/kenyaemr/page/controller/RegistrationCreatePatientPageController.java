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

import org.openmrs.module.appframework.AppUiUtil;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.session.Session;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Create patient page controller
 */
public class RegistrationCreatePatientPageController {
	
	public void controller(Session session,
	                       @RequestParam(required = false, value = "returnApp") String returnApp,
	                       PageModel model) {

		AppUiUtil.startApp("kenyaemr.registration", session);

		model.addAttribute("returnApp", returnApp);
	}
}