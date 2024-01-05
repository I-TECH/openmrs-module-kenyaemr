/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
	private static final String KENYAEMR_HELPDIALOG_URL = EmrConstants.MODULE_ID + "/helpDialog.page";

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
	/**
	 * Handles requests to helpDialog.form. If user is authenticated they will be redirected to this modules's home
	 * page. If not they will be forwarded to this module's help dialog page.
	 */
	@RequestMapping("/helpDialog.form")
	public String showOurHelpDialogPage() {
		return Context.isAuthenticated() ? ("redirect:/" + KENYAEMR_HELPDIALOG_URL) : ("forward:/" + KENYAEMR_HELPDIALOG_URL);
	}
}
