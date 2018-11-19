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

import org.openmrs.User;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.ui.framework.page.PageModel;

import javax.servlet.http.HttpSession;

/**
 * My profile page controller
 */
public class ProfilePageController {

	public void controller(PageModel model, HttpSession httpSession) {

		// Seems we need to reload the user object to avoid lazy initialization exceptions
		User user = Context.getUserService().getUser(Context.getUserContext().getAuthenticatedUser().getId());

		model.addAttribute("user", user);
		model.addAttribute("person", user.getPerson());

		// If temp password is being passed, in tell view to display change password dialog
		model.addAttribute("tempPassword", httpSession.getAttribute(EmrWebConstants.SESSION_ATTR_RESET_PASSWORD));

		httpSession.removeAttribute(EmrWebConstants.SESSION_ATTR_RESET_PASSWORD);
	}
}