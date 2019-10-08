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

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.kenyaui.annotation.PublicPage;
import org.openmrs.ui.framework.WebConstants;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;

/**
 * Login page controller
 */
@PublicPage
public class LoginPageController {

	public void controller(@RequestParam(value = "redirect", required = false) String redirect, PageModel model) {
		String loginServletUrl = "/" + WebConstants.CONTEXT_PATH + "/ms/legacyui/loginServlet";

		if (StringUtils.isNotEmpty(redirect)) {
			// Prepend context path to application URLs as LoginServlet expects this
			if (!redirect.startsWith("/" + WebConstants.CONTEXT_PATH)) {
				redirect = "/" + WebConstants.CONTEXT_PATH + "/" + redirect;
			}

			loginServletUrl += "?redirect=" + URLEncoder.encode(redirect);
		}

		model.addAttribute("loginServletUrl", loginServletUrl);
	}
}