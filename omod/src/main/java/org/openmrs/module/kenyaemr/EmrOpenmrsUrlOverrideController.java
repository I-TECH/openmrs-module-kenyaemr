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

import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Overrides /index.htm, /login.htm and /forgotPassword.form so users don't see the legacy OpenMRS UI
 */
@Controller
public class EmrOpenmrsUrlOverrideController {

	private static final String OPENMRS_HOME_URL = "index.htm";
	private static final String OPENMRS_LOGIN_URL = "login.htm";
	private static final String KENYAEMR_HOME_URL = EmrConstants.MODULE_ID + "/home.page";
	private static final String KENYAEMR_LOGIN_URL = EmrConstants.MODULE_ID + "/login.page";
	private static final String KENYAEMR_FORGOTPASSWORD_URL = EmrConstants.MODULE_ID + "/forgotPassword.page";

	/**
	 * Handles requests for index.htm. If user is authenticated they will be forwarded to this module's home page. If
	 * not they will be redirected to this module's login page.
	 */
	@RequestMapping("/index.htm")
	public String showOurHomePage() {
		return Context.isAuthenticated() ? ("forward:/" + KENYAEMR_HOME_URL) : ("redirect:/" + OPENMRS_LOGIN_URL);
	}

	/**
	 * Handles requests for login.htm. If user is authenticated they will be redirected to this modules's home page. If
	 * not they will be forwarded to this module's login page.
	 */
	@RequestMapping("/login.htm")
	public String showOurLoginPage() {
		return Context.isAuthenticated() ? ("redirect:/" + OPENMRS_HOME_URL) : ("forward:/" + KENYAEMR_LOGIN_URL);
	}

	/**
	 * Handles requests to forgotPassword.form. If user is authenticated they will be redirected to this modules's home
	 * page. If not they will be forwarded to this module's forgot password page.
	 */
	@RequestMapping("/forgotPassword.form")
	public String showOurForgotPasswordPage() {
		return Context.isAuthenticated() ? ("redirect:/" + OPENMRS_HOME_URL) : ("forward:/" + KENYAEMR_FORGOTPASSWORD_URL);
	}
}