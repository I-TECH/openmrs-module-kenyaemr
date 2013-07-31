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

package org.openmrs.module.kenyaemr.page.controller.admin;

import java.util.Collection;
import java.util.List;

import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Editing user and provider accounts page
 */
@AppPage(EmrConstants.APP_ADMIN)
public class EditAccountPageController {
	
	public void controller(@RequestParam(value = "personId", required = false) Person person,
	                       PageModel model) {

		model.addAttribute("person", person);

		if (person != null) {
			model.addAttribute("user", getUser(person));
			model.addAttribute("provider", getProvider(person));
		}
		else {
			model.addAttribute("user", null);
			model.addAttribute("provider", null);
		}
	}

	/**
	 * Gets the first user associated with the given person
	 * @param person the person
	 * @return the user
	 */
    private User getUser(Person person) {
    	List<User> users = Context.getUserService().getUsersByPerson(person, true);
    	if (users == null || users.size() == 0) {
    		return null;
    	}
    	return users.get(0);
    }

	/**
	 * Gets the first provider associated with the given person
	 * @param person the person
	 * @return the provider
	 */
    private Provider getProvider(Person person) {
    	Collection<Provider> providers = Context.getProviderService().getProvidersByPerson(person);
    	if (providers == null || providers.size() == 0) {
    		return null;
    	}
    	return providers.iterator().next();
    }
}