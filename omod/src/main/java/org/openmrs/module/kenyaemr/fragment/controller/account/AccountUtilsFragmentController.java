/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.fragment.controller.account;

import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaui.annotation.AppAction;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * AJAX utility methods for accounts
 */
public class AccountUtilsFragmentController {

	/**
	 * Retires the given user
	 * @param user the user
	 * @return a success result
	 */
	@AppAction(EmrConstants.APP_ADMIN)
	public Object retireUser(@RequestParam("userId") User user) {
		Context.getUserService().retireUser(user, null);
		return new SuccessResult("Disabled: " + user.getUsername());
	}

	/**
	 * Un-retires the given user
	 * @param user the user
	 * @return a success result
	 */
	@AppAction(EmrConstants.APP_ADMIN)
	public Object unretireUser(@RequestParam("userId") User user) {
		Context.getUserService().unretireUser(user);
		return new SuccessResult("Enabled: " + user.getUsername());
	}
}