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
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor to catch requests to controllers outside of KenyaEMR and any add-on modules
 */
public class EmrExternalUrlInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private KenyaUiUtils kenyaUi;

	protected static final Log log = LogFactory.getLog(EmrExternalUrlInterceptor.class);

	/**
	 * @see HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Intercepting request: " + request.getRequestURI() + " -> " + handler);
		}

		// Allow any request to the URL override controller (handles index.htm, login.htm and forgotPassword.form)
		if (handler instanceof EmrURLOverrideController) {
			return true;
		}

		// Allow any request to /module/kenyaemr/generateField.htm
		if (handler instanceof FieldGeneratorController) {
			return true;
		}

		// TODO implement a whitelist which allows only certain uiframework managed controllers.
		// Not so important for now whilst all uiframework managed content is valid. Could use method below to parse URL
		// to get moduleid and whitelist those

		// Allow any request to the UI Framework
		if (handler.getClass().getPackage().getName().equals("org.openmrs.module.uiframework")) {
			return true;
		}

		// Only allow other requests if user is a super-user
		User authenticatedUser = Context.getAuthenticatedUser();
		boolean allowRequest = authenticatedUser != null ? authenticatedUser.isSuperUser() : false;

		if (!allowRequest) {
			log.warn("Prevented request to " + request.getRequestURI() + " by non-super user");

			// Redirect to login page
			kenyaUi.notifyError(request.getSession(), "Invalid external page access by non-super user");
			response.sendRedirect(request.getContextPath() + "/login.htm");
		}

		return allowRequest;
	}

	/**
	 * Gets a cleaned up version of the incoming request URL with the context path removed as well as any beginning slash
	 * @param request the request
	 * @return the URL
	 */
	private String getRequestUrlWithoutContext(HttpServletRequest request) {
		String path = request.getRequestURI();
		String contextPath = request.getContextPath();

		if (path.startsWith(contextPath)) {
			path = path.substring(contextPath.length());
		}

		if (path.startsWith("/")) {
			path = path.substring(1);
		}

		return path;
	}
}