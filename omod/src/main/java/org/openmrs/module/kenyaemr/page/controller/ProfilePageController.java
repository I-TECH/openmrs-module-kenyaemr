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

import org.openmrs.User;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.session.Session;

/**
 * My profile page controller
 */
public class ProfilePageController {

	public void controller(PageModel model, Session session) {

		User user = Context.getUserContext().getAuthenticatedUser();
		if (user != null) {
			model.addAttribute("user", user);
			model.addAttribute("person", user.getPerson());
		}
		else {
			throw new APIAuthenticationException("You must be logged in");
		}
	}
}