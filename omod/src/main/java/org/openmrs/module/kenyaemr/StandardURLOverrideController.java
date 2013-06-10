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
package org.openmrs.module.kenyaemr;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Overrides /index.htm, /login.htm and /forgotPassword.form so users don't see the legacy OpenMRS UI
 */
@Controller
public class StandardURLOverrideController {
	
	@RequestMapping("/index.htm")
	public String showOurHomepage() {
		return "forward:/" + KenyaEmrConstants.MODULE_ID + "/home.page";
	}
	
	@RequestMapping("/login.htm")
	public String showLoginHomepage() {
		return "forward:/" + KenyaEmrConstants.MODULE_ID + "/login.page";
	}

	@RequestMapping("/forgotPassword.form")
	public String showOurForgotPasswordPage() {
		return "forward:/" + KenyaEmrConstants.MODULE_ID + "/forgotPassword.page";
	}
}