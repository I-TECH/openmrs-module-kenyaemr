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
import org.openmrs.module.kenyaemr.util.ServerInformation;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Provides JSON formatted system monitoring information
 */
@Controller
public class SystemMonitorController {

	protected static final Log log = LogFactory.getLog(SystemMonitorController.class);

	@RequestMapping(value = "/sysmon.form", method = RequestMethod.GET)
	public void get(HttpServletRequest request, HttpServletResponse response) throws IOException {
		SimpleObject output;

		if (checkAccess(request)) {
			log.info("Accepting system monitoring request from " + request.getRemoteAddr());
			output = getMonitoredData();
		}
		else {
			log.warn("Rejecting system monitoring request from " + request.getRemoteAddr() + " (server address is " + request.getLocalAddr() + ")");
			response.setStatus(403);
			output = SimpleObject.create("error", "access denied");
		}

		response.setContentType("application/json");
		response.getWriter().write(output.toJson());
	}

	/**
	 * Gets the data which is monitored
	 * @return the simplified data
	 */
	protected SimpleObject getMonitoredData() {
		SimpleObject stats = new SimpleObject();
		stats.put("server", ServerInformation.getAllInformation());
		return stats;
	}

	/**
	 * Checks if incoming request for system monitoring data should be allowed
	 * @param request the request
	 * @return true if request should be allowed
	 */
	protected boolean checkAccess(HttpServletRequest request) {
		// For now just check that request is local
		return OpenmrsUtil.nullSafeEquals(request.getRemoteAddr(), request.getLocalAddr());
	}
}