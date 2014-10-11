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
		String loginServletUrl = "/" + WebConstants.CONTEXT_PATH + "/loginServlet";

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