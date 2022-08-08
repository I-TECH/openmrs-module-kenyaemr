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