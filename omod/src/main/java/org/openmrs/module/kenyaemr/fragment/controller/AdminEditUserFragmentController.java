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
package org.openmrs.module.kenyaemr.fragment.controller;

import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 */
public class AdminEditUserFragmentController {
	
	public void controller(PageModel sharedPageModel,
	                       FragmentModel model) {
		model.addAttribute("user", sharedPageModel.getAttribute("user"));
	}
	
	public Object retireUser(@RequestParam("userId") User user) {
		Context.getUserService().retireUser(user, null);
		return new SuccessResult("Disabled: " + user.getUsername());
	}
	
	public Object unretireUser(@RequestParam("userId") User user) {
		Context.getUserService().unretireUser(user);
		return new SuccessResult("Enabled: " + user.getUsername());
	}
	
}
