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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.metadata.SecurityMetadata;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

/**
 * Interceptor to catch requests to controllers outside of KenyaEMR and any add-on modules
 */
public class EmrExternalUrlInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	protected KenyaUiUtils kenyaUi;

	@Autowired
	protected AdministrationService adminService;

	protected static final String[] BASE_CONTROLLER_WHITELIST = {
			"org.openmrs.module.htmlformentry.web.controller", // Required for concept widget in HFE
			"org.openmrs.module.kenyaemr",
			"org.openmrs.module.uiframework"
	};

	protected Set<String> controllerWhitelist = null;

	protected static final Log log = LogFactory.getLog(EmrExternalUrlInterceptor.class);

	/**
	 * @see HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String controllerPackage = handler.getClass().getPackage().getName();

		// Check Spring controller package name against whitelist
		for (String whitelisted : getControllerWhitelist()) {
			if (controllerPackage.startsWith(whitelisted)) {
				return true;
			}
		}

		// TODO implement a whitelist which allows only certain uiframework managed controllers.

		// Only allow other requests if user has "View Legacy Interface" privilege
		User authenticatedUser = Context.getAuthenticatedUser();
		boolean allowRequest = authenticatedUser != null && authenticatedUser.hasPrivilege(SecurityMetadata._Privilege.VIEW_LEGACY_INTERFACE);

		if (!allowRequest) {
			log.warn("Prevented request to " + request.getRequestURI() + " by user without '" + SecurityMetadata._Privilege.VIEW_LEGACY_INTERFACE + "' privilege");

			// Redirect to login page
			kenyaUi.notifyError(request.getSession(), "Invalid external page access");
			response.sendRedirect(request.getContextPath() + "/login.htm");
		}

		return allowRequest;
	}

	/**
	 * Gets the controller whitelist, loading and parsing it from the global property if necessary
	 * @return the whitelist
	 */
	protected Set<String> getControllerWhitelist() {
		if (controllerWhitelist == null) {
			controllerWhitelist = new HashSet<String>();

			// Add required values
			for (String required : BASE_CONTROLLER_WHITELIST) {
				controllerWhitelist.add(required);
			}

			// Load and parse custom values from global property
			String csv = adminService.getGlobalProperty(EmrConstants.GP_CONTROLLER_WHITELIST);
			if (csv != null) {
				controllerWhitelist.addAll(EmrUtils.parseCsv(csv));
			}
		}
		return controllerWhitelist;
	}
}