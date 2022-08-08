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
			"org.openmrs.module.uiframework",
			"org.openmrs.module.sync2",
			"org.openmrs.module.webservices.rest",
			"org.openmrs.module.atomfeed"
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
		/*User authenticatedUser = Context.getAuthenticatedUser();
		boolean allowRequest = authenticatedUser != null && authenticatedUser.hasPrivilege(SecurityMetadata._Privilege.VIEW_LEGACY_INTERFACE);

		if (!allowRequest) {
			log.warn("Prevented request to " + request.getRequestURI() + " by user without '" + SecurityMetadata._Privilege.VIEW_LEGACY_INTERFACE + "' privilege");

			// Redirect to login page
			kenyaUi.notifyError(request.getSession(), "Invalid external page access");
			response.sendRedirect(request.getContextPath() + "/login.htm");
		}

		return allowRequest;*/
		return true;
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