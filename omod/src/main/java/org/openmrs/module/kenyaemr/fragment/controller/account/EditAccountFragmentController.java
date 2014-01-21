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

package org.openmrs.module.kenyaemr.fragment.controller.account;

import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.page.PageModel;

/**
 * Edit account form fragment
 */
public class EditAccountFragmentController {
	
	public void controller(PageModel sharedPageModel, FragmentModel model) {
		Person person = (Person) sharedPageModel.getAttribute("person");
		User user = (User) sharedPageModel.getAttribute("user");
		Provider provider = (Provider) sharedPageModel.getAttribute("provider");

		model.addAttribute("person", person);
		model.addAttribute("user", user);
		model.addAttribute("provider", provider);
	}
}