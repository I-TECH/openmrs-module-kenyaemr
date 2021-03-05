/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.kenyaemr.util.EmrUtils;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * The main controller.
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/kemr")
public class EmrRestController extends BaseRestController {

	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * Generates KenyaEMR related metrics
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/getemrmetrics")
	@ResponseBody
	public Object getKenyaEMRDetails(HttpServletRequest request) {

		return "{\n" +
				"    \"EmrName\":\"KenyaEMR\",\n" +
				"    \"EmrVersion\":\"" + EmrUtils.getKenyaemrVersion() + "\",\n" +
				"    \"LastLoginDate\":\"" + EmrUtils.getLastLogin() + "\",\n" +
				"    \"LastMoH731RunDate\":\"" + EmrUtils.getDateofLastMOH731() + "\"" +
				"}";
	}

	/**
	 * @see BaseRestController#getNamespace()
	 */

	@Override
	public String getNamespace() {
		return "v1/kenyaemr";
	}

}
