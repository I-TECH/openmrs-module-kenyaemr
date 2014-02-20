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